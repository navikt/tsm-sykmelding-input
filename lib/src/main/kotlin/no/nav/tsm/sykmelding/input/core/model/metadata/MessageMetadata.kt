package no.nav.tsm.sykmelding.input.core.model.metadata

import java.time.OffsetDateTime
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger(MessageMetadata::class.java)

enum class MetadataType {
    DIGITAL,
    ENKEL,
    EMOTTAK,
    UTENLANDSK_SYKMELDING,
    PAPIRSYKMELDING,
    EGENMELDT,
}

sealed interface MessageMetadata {
    val type: MetadataType

    data class Digital(val orgnummer: String) : MessageMetadata {
        override val type: MetadataType = MetadataType.DIGITAL
    }

    data class Papir(
        val msgInfo: MessageInfo,
        val sender: Organisasjon,
        val receiver: Organisasjon,
        val journalPostId: String,
    ) : MessageMetadata {
        override val type = MetadataType.PAPIRSYKMELDING
    }

    data class Utenlandsk(val land: String, val journalPostId: String) : MessageMetadata {
        override val type: MetadataType = MetadataType.UTENLANDSK_SYKMELDING
    }

    sealed interface Xml : MessageMetadata {

        data class Egenmeldt(val msgInfo: MessageInfo) : Xml {
            override val type: MetadataType = MetadataType.EGENMELDT
        }

        sealed interface Emottak : Xml {
            val msgInfo: MessageInfo
            val sender: Organisasjon
            val receiver: Organisasjon
            val vedlegg: List<String>?

            data class EDI(
                val mottakenhetBlokk: MottakenhetBlokk,
                val ack: Ack,
                override val msgInfo: MessageInfo,
                override val sender: Organisasjon,
                override val receiver: Organisasjon,
                val pasient: Pasient?,
                override val vedlegg: List<String>?,
            ) : Emottak {
                override val type = MetadataType.EMOTTAK
            }

            data class Legacy(
                override val msgInfo: MessageInfo,
                override val sender: Organisasjon,
                override val receiver: Organisasjon,
                override val vedlegg: List<String>?,
            ) : Emottak {
                override val type = MetadataType.ENKEL
            }
        }
    }
}

enum class AckType {
    JA,
    NEI,
    KUN_VED_FEIL,
    IKKE_OPPGITT,
    UGYLDIG;

    companion object {
        fun parse(value: String?): AckType {
            return when (value) {
                null -> IKKE_OPPGITT
                "J" -> JA
                "N" -> NEI
                "F" -> KUN_VED_FEIL
                "" -> UGYLDIG
                else -> throw IllegalArgumentException("Unrecognized ack type: $value")
            }
        }
    }
}

data class Ack(val ackType: AckType)

enum class Meldingstype {
    SYKMELDING;

    companion object {
        fun parse(v: String): Meldingstype =
            when (v) {
                "SYKMELD" -> SYKMELDING
                "SYKMELD_EKHO" -> {
                    log.warn("Sykmelding has incorrect type: $v, should be: SYKMELD")
                    SYKMELDING
                }
                else -> throw IllegalArgumentException("Ukjent meldingstype: $v")
            }
    }
}

data class MessageInfo(
    val type: Meldingstype,
    val genDate: OffsetDateTime,
    val msgId: String,
    val migVersjon: String?,
)

data class MottakenhetBlokk(
    val ediLogid: String,
    val avsender: String,
    val ebXMLSamtaleId: String,
    val mottaksId: String?,
    val meldingsType: String,
    val avsenderRef: String,
    val avsenderFnrFraDigSignatur: String?,
    val mottattDato: OffsetDateTime,
    val orgnummer: String?,
    val avsenderOrgNrFraDigSignatur: String?,
    val partnerReferanse: String,
    val herIdentifikator: String,
    val ebRole: String,
    val ebService: String,
    val ebAction: String,
)
