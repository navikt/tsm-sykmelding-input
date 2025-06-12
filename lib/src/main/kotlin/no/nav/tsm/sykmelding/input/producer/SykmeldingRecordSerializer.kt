package no.nav.tsm.sykmelding.input.producer

import no.nav.tsm.sykmelding.input.core.model.SykmeldingRecord
import no.nav.tsm.sykmelding.input.core.model.sykmeldingObjectMapper
import org.apache.kafka.common.serialization.Serializer

internal class SykmeldingRecordSerializer : Serializer<SykmeldingRecord?> {
    override fun serialize(topic: String, sykmeldingRecord: SykmeldingRecord?): ByteArray? {
        return when (sykmeldingRecord) {
            null -> null
            else -> sykmeldingObjectMapper.writeValueAsBytes(sykmeldingRecord)
        }
    }
}
