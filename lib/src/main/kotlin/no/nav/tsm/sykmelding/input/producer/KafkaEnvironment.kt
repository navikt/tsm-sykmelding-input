package no.nav.tsm.sykmelding.input.producer

class KafkaEnvironment(
    val kafkaBrokers: String = getEnvVar("KAFKA_BROKERS"),
    val kafkaClientId: String = getEnvVar("HOSTNAME"),
    val kafkaTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val kafkaKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val kafkaCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val sourceApp: String = getEnvVar("NAIS_APP_NAME"),
    val sourceNamespace: String = getEnvVar("NAIS_NAMESPACE"),
) {
    companion object {
        fun getEnvVar(varName: String, defaultValue: String? = null) =
            System.getenv(varName)
                ?: defaultValue
                ?: throw RuntimeException("Missing required variable \"$varName\"")
    }
}
