package no.nav.tsm.sykmelding.input.core.model

import kotlin.reflect.KClass
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageMetadata
import no.nav.tsm.sykmelding.input.core.model.metadata.MetadataType
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.module.SimpleModule

class SykmeldingModule : SimpleModule() {
    init {
        addDeserializer(SykmeldingRecord::class.java, SykmeldingRecordDeserializer())
        addDeserializer(Sykmelding::class.java, SykmeldingDeserializer())
        addDeserializer(Aktivitet::class.java, AktivitetDeserializer())
        addDeserializer(ArbeidsgiverInfo::class.java, ArbeidsgiverInfoDeserializer())
        addDeserializer(IArbeid::class.java, IArbeidDeserializer())
        addDeserializer(Rule::class.java, RuleDeserializer())
        addDeserializer(MessageMetadata.Xml::class.java, XmlMessageMetadataDeserializer())
        addDeserializer(SykmeldingMeta::class.java, SykmeldingMetaDeserializer())
    }
}

abstract class CustomDeserializer<T : Any> : ValueDeserializer<T>() {
    abstract fun getClass(type: String): KClass<out T>

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val json = ctxt.readTree(p)
        val type = json.get("type").asString()
        val clazz = getClass(type)
        return ctxt.readTreeAsValue(json, clazz.java)
    }
}

class SykmeldingRecordDeserializer : ValueDeserializer<SykmeldingRecord>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SykmeldingRecord {
        val json = ctxt.readTree(p)
        val sykmeldingType =
            json.get("sykmelding")?.get("type")?.asString()
                ?: throw IllegalArgumentException(
                    "Missing sykmelding.type in SykmeldingRecord JSON"
                )
        val metadataType =
            json.get("metadata")?.get("type")?.asString()
                ?: throw IllegalArgumentException("Missing metadata.type in SykmeldingRecord JSON")
        val parsedSykmeldingType = SykmeldingType.valueOf(sykmeldingType)
        val parsedMetadataType = MetadataType.valueOf(metadataType)
        val allowedMetadataTypes =
            when (parsedSykmeldingType) {
                SykmeldingType.DIGITAL -> setOf(MetadataType.DIGITAL)
                SykmeldingType.XML ->
                    setOf(MetadataType.ENKEL, MetadataType.EMOTTAK, MetadataType.EGENMELDT)
                SykmeldingType.PAPIR -> setOf(MetadataType.PAPIRSYKMELDING)
                SykmeldingType.UTENLANDSK -> setOf(MetadataType.UTENLANDSK_SYKMELDING)
            }
        require(parsedMetadataType in allowedMetadataTypes) {
            "Mismatched metadata.type=$parsedMetadataType for sykmelding.type=$parsedSykmeldingType (expected one of $allowedMetadataTypes)"
        }
        val clazz: KClass<out SykmeldingRecord> =
            when (parsedSykmeldingType) {
                SykmeldingType.DIGITAL -> SykmeldingRecord.Digital::class
                SykmeldingType.XML -> SykmeldingRecord.Xml::class
                SykmeldingType.PAPIR -> SykmeldingRecord.Papir::class
                SykmeldingType.UTENLANDSK -> SykmeldingRecord.Utenlandsk::class
            }
        return ctxt.readTreeAsValue(json, clazz.java)
    }
}

class SykmeldingDeserializer : CustomDeserializer<Sykmelding>() {
    override fun getClass(type: String): KClass<out Sykmelding> {
        return when (SykmeldingType.valueOf(type)) {
            SykmeldingType.XML -> Sykmelding.Xml::class
            SykmeldingType.PAPIR -> Sykmelding.Papir::class
            SykmeldingType.UTENLANDSK -> Sykmelding.Utenlandsk::class
            SykmeldingType.DIGITAL -> Sykmelding.Digital::class
        }
    }
}

class XmlMessageMetadataDeserializer : CustomDeserializer<MessageMetadata.Xml>() {
    override fun getClass(type: String): KClass<out MessageMetadata.Xml> {
        return when (MetadataType.valueOf(type)) {
            MetadataType.ENKEL -> MessageMetadata.Xml.Emottak.Legacy::class
            MetadataType.EMOTTAK -> MessageMetadata.Xml.Emottak.EDI::class
            MetadataType.EGENMELDT -> MessageMetadata.Xml.Egenmeldt::class
            MetadataType.DIGITAL,
            MetadataType.UTENLANDSK_SYKMELDING,
            MetadataType.PAPIRSYKMELDING ->
                throw IllegalArgumentException(
                    "Invalid metadata type for MessageMetadata.Xml: $type"
                )
        }
    }
}

class RuleDeserializer : CustomDeserializer<Rule>() {

    override fun getClass(type: String): KClass<out Rule> {
        return when (RuleType.valueOf(type)) {
            RuleType.INVALID -> Rule.Invalid::class
            RuleType.PENDING -> Rule.Pending::class
            RuleType.OK -> Rule.OK::class
        }
    }
}

class IArbeidDeserializer : CustomDeserializer<IArbeid>() {
    override fun getClass(type: String): KClass<out IArbeid> {
        return when (IArbeidType.valueOf(type)) {
            IArbeidType.ER_I_ARBEID -> IArbeid.ErIArbeid::class
            IArbeidType.ER_IKKE_I_ARBEID -> IArbeid.ErIkkeIArbeid::class
        }
    }
}

class ArbeidsgiverInfoDeserializer : CustomDeserializer<ArbeidsgiverInfo>() {
    override fun getClass(type: String): KClass<out ArbeidsgiverInfo> {
        return when (ARBEIDSGIVER_TYPE.valueOf(type)) {
            ARBEIDSGIVER_TYPE.EN_ARBEIDSGIVER -> ArbeidsgiverInfo.En::class
            ARBEIDSGIVER_TYPE.FLERE_ARBEIDSGIVERE -> ArbeidsgiverInfo.Flere::class
            ARBEIDSGIVER_TYPE.INGEN_ARBEIDSGIVER -> ArbeidsgiverInfo.Ingen::class
        }
    }
}

class AktivitetDeserializer : CustomDeserializer<Aktivitet>() {
    override fun getClass(type: String): KClass<out Aktivitet> {
        return when (Aktivitetstype.valueOf(type)) {
            Aktivitetstype.AKTIVITET_IKKE_MULIG -> Aktivitet.IkkeMulig::class
            Aktivitetstype.AVVENTENDE -> Aktivitet.Avventende::class
            Aktivitetstype.BEHANDLINGSDAGER -> Aktivitet.Behandlingsdager::class
            Aktivitetstype.GRADERT -> Aktivitet.Gradert::class
            Aktivitetstype.REISETILSKUDD -> Aktivitet.Reisetilskudd::class
        }
    }
}

class SykmeldingMetaDeserializer : CustomDeserializer<SykmeldingMeta>() {
    override fun getClass(type: String): KClass<out SykmeldingMeta> {
        return when (SykmeldingType.valueOf(type)) {
            SykmeldingType.XML -> SykmeldingMeta.Legacy::class
            SykmeldingType.PAPIR -> SykmeldingMeta.Legacy::class
            SykmeldingType.UTENLANDSK -> SykmeldingMeta.Legacy::class
            SykmeldingType.DIGITAL -> SykmeldingMeta.Digital::class
        }
    }
}
