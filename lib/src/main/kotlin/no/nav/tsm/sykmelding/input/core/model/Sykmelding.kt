package no.nav.tsm.sykmelding.input.core.model

import java.time.LocalDate
import java.time.OffsetDateTime
import no.nav.tsm.sykmelding.input.core.model.metadata.Adresse
import no.nav.tsm.sykmelding.input.core.model.metadata.HelsepersonellKategori
import no.nav.tsm.sykmelding.input.core.model.metadata.Kontaktinfo
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageMetadata
import no.nav.tsm.sykmelding.input.core.model.metadata.Navn
import no.nav.tsm.sykmelding.input.core.model.metadata.PersonId

sealed interface SykmeldingRecord {
    val metadata: MessageMetadata
    val sykmelding: Sykmelding
    val validation: ValidationResult

    data class Digital(
        override val metadata: MessageMetadata.Digital,
        override val sykmelding: Sykmelding.Digital,
        override val validation: ValidationResult,
    ) : SykmeldingRecord

    data class Xml(
        override val metadata: MessageMetadata.Xml,
        override val sykmelding: Sykmelding.Xml,
        override val validation: ValidationResult,
    ) : SykmeldingRecord

    data class Papir(
        override val metadata: MessageMetadata.Papir,
        override val sykmelding: Sykmelding.Papir,
        override val validation: ValidationResult,
    ) : SykmeldingRecord

    data class Utenlandsk(
        override val metadata: MessageMetadata.Utenlandsk,
        override val sykmelding: Sykmelding.Utenlandsk,
        override val validation: ValidationResult,
    ) : SykmeldingRecord
}

data class Pasient(
    val navn: Navn?,
    val navKontor: String?,
    val navnFastlege: String?,
    val fnr: String,
    val kontaktinfo: List<Kontaktinfo>,
)

data class Behandler(
    val navn: Navn,
    val adresse: Adresse?,
    val ids: List<PersonId>,
    val kontaktinfo: List<Kontaktinfo>,
)

data class Sykmelder(val ids: List<PersonId>, val helsepersonellKategori: HelsepersonellKategori)

enum class SykmeldingType {
    DIGITAL,
    XML,
    PAPIR,
    UTENLANDSK,
}

sealed interface Sykmelding {
    val type: SykmeldingType
    val id: String
    val metadata: SykmeldingMeta
    val pasient: Pasient
    val medisinskVurdering: MedisinskVurdering
    val aktivitet: List<Aktivitet>

    sealed interface Nasjonal : Sykmelding {
        val behandler: Behandler
        val sykmelder: Sykmelder
        val arbeidsgiver: ArbeidsgiverInfo
        val bistandNav: BistandNav?
        val tilbakedatering: Tilbakedatering?

        sealed interface Legacy : Nasjonal {
            val prognose: Prognose?
            val tiltak: Tiltak?
            val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>?
        }
    }

    data class Utenlandsk(
        override val id: String,
        override val metadata: SykmeldingMeta.Legacy,
        override val pasient: Pasient,
        override val medisinskVurdering: MedisinskVurdering.Legacy,
        override val aktivitet: List<Aktivitet>,
        val utenlandskInfo: UtenlandskInfo,
    ) : Sykmelding {
        override val type = SykmeldingType.UTENLANDSK
    }

    data class Digital(
        override val id: String,
        override val metadata: SykmeldingMeta.Digital,
        override val pasient: Pasient,
        override val medisinskVurdering: MedisinskVurdering.Digital,
        override val aktivitet: List<Aktivitet>,
        override val behandler: Behandler,
        override val sykmelder: Sykmelder,
        override val arbeidsgiver: ArbeidsgiverInfo,
        override val tilbakedatering: Tilbakedatering?,
        override val bistandNav: BistandNav?,
        val utdypendeSporsmal: List<UtdypendeSporsmal>?,
    ) : Nasjonal {
        override val type = SykmeldingType.DIGITAL
    }

    data class Xml(
        override val id: String,
        override val metadata: SykmeldingMeta.Legacy,
        override val pasient: Pasient,
        override val medisinskVurdering: MedisinskVurdering.Legacy,
        override val aktivitet: List<Aktivitet>,
        override val arbeidsgiver: ArbeidsgiverInfo,
        override val behandler: Behandler,
        override val sykmelder: Sykmelder,
        override val prognose: Prognose?,
        override val tiltak: Tiltak?,
        override val bistandNav: BistandNav?,
        override val tilbakedatering: Tilbakedatering?,
        override val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>?,
    ) : Nasjonal.Legacy {
        override val type = SykmeldingType.XML
    }

    data class Papir(
        override val id: String,
        override val metadata: SykmeldingMeta.Legacy,
        override val pasient: Pasient,
        override val medisinskVurdering: MedisinskVurdering.Legacy,
        override val aktivitet: List<Aktivitet>,
        override val arbeidsgiver: ArbeidsgiverInfo,
        override val behandler: Behandler,
        override val sykmelder: Sykmelder,
        override val prognose: Prognose?,
        override val tiltak: Tiltak?,
        override val bistandNav: BistandNav?,
        override val tilbakedatering: Tilbakedatering?,
        override val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>?,
    ) : Nasjonal.Legacy {
        override val type = SykmeldingType.PAPIR
    }
}

data class AvsenderSystem(val navn: String, val versjon: String)

sealed interface SykmeldingMeta {
    val mottattDato: OffsetDateTime
    val genDate: OffsetDateTime
    val avsenderSystem: AvsenderSystem

    data class Legacy(
        override val mottattDato: OffsetDateTime,
        override val genDate: OffsetDateTime,
        override val avsenderSystem: AvsenderSystem,
        val behandletTidspunkt: OffsetDateTime,
        val regelsettVersjon: String?,
        val strekkode: String?,
    ) : SykmeldingMeta

    data class Digital(
        override val mottattDato: OffsetDateTime,
        override val genDate: OffsetDateTime,
        override val avsenderSystem: AvsenderSystem,
    ) : SykmeldingMeta
}

data class BistandNav(val bistandUmiddelbart: Boolean, val beskrivBistand: String?)

data class Tiltak(val tiltakNav: String?, val andreTiltak: String?)

data class Prognose(
    val arbeidsforEtterPeriode: Boolean,
    val hensynArbeidsplassen: String?,
    val arbeid: IArbeid?,
)

data class Tilbakedatering(val kontaktDato: LocalDate?, val begrunnelse: String?)

data class UtenlandskInfo(
    val land: String,
    val folkeRegistertAdresseErBrakkeEllerTilsvarende: Boolean,
    val erAdresseUtland: Boolean?,
)

data class SporsmalSvar(
    val sporsmal: String?,
    val svar: String,
    val restriksjoner: List<SvarRestriksjon>,
)

enum class SvarRestriksjon {
    SKJERMET_FOR_ARBEIDSGIVER,
    SKJERMET_FOR_PASIENT,
    SKJERMET_FOR_NAV,
}
