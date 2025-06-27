package no.nav.tsm.sykmelding.input.producer

import java.util.Properties
import no.nav.tsm.sykmelding.input.core.model.SykmeldingRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory

interface SykmeldingInputProducer {
    fun sendSykmelding(sykmelding: SykmeldingRecord)

    fun tombstoneSykmelding(sykmeldingId: String)
}

class SykmeldingInputKafkaInputFactory private constructor() {
    companion object {
        private val log = LoggerFactory.getLogger(SykmeldingInputKafkaProducer::class.java)
        private const val TOPIC = "tsm.sykmeldinger-input"

        fun create(): SykmeldingInputProducer {
            val kafkaEnvironment = KafkaEnvironment()
            val properties =
                Properties().apply {
                    this[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] =
                        kafkaEnvironment.kafkaBrokers
                    this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SSL"
                    this[SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG] = "jks"
                    this[SslConfigs.SSL_KEYSTORE_TYPE_CONFIG] = "PKCS12"
                    this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] =
                        kafkaEnvironment.kafkaTruststorePath
                    this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] =
                        kafkaEnvironment.kafkaCredstorePassword
                    this[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] =
                        kafkaEnvironment.kafkaKeystorePath
                    this[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] =
                        kafkaEnvironment.kafkaCredstorePassword
                    this[ProducerConfig.ACKS_CONFIG] = "all"
                    this[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
                    this[ProducerConfig.CLIENT_ID_CONFIG] =
                        "${kafkaEnvironment.kafkaClientId}-producer"
                    this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
                    this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
                        SykmeldingRecordSerializer::class.java
                    this[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "gzip"
                }
            return SykmeldingInputKafkaProducer(
                KafkaProducer(properties),
                TOPIC,
                kafkaEnvironment.sourceApp,
                kafkaEnvironment.sourceNamespace,
            )
        }
    }
}

internal class SykmeldingInputKafkaProducer(
    private val kafkaProducer: KafkaProducer<String, SykmeldingRecord>,
    private val topic: String,
    private val sourceApp: String,
    private val sourceNamespace: String,
) : SykmeldingInputProducer {
    companion object {
        private const val SOURCE_APP = "source_app"
        private const val SOURCE_NAMESPACE = "source_namespace"
    }

    override fun sendSykmelding(sykmelding: SykmeldingRecord) {
        val producerRecord = ProducerRecord(topic, sykmelding.sykmelding.id, sykmelding)
        producerRecord.headers().add(SOURCE_APP, sourceApp.toByteArray())
        producerRecord.headers().add(SOURCE_NAMESPACE, sourceNamespace.toByteArray())
        kafkaProducer.send(producerRecord).get()
    }

    override fun tombstoneSykmelding(sykmeldingId: String) {
        val producerRecord: ProducerRecord<String, SykmeldingRecord> =
            ProducerRecord(topic, sykmeldingId, null)
        producerRecord.headers().add(SOURCE_APP, sourceApp.toByteArray())
        producerRecord.headers().add(SOURCE_NAMESPACE, sourceNamespace.toByteArray())
        kafkaProducer.send(producerRecord)
    }
}
