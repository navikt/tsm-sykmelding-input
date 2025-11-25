package no.nav.tsm.sykmelding.input.core.model

import java.time.LocalDate
import java.time.OffsetDateTime
import no.nav.tsm.sykmelding.input.core.model.metadata.Adresse
import no.nav.tsm.sykmelding.input.core.model.metadata.HelsepersonellKategori
import no.nav.tsm.sykmelding.input.core.model.metadata.Kontaktinfo
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageMetadata
import no.nav.tsm.sykmelding.input.core.model.metadata.Navn
import no.nav.tsm.sykmelding.input.core.model.metadata.PersonId

data class SykmeldingRecord(
    val metadata: MessageMetadata,
    val sykmelding: Sykmelding,
    val validation: ValidationResult,
)

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
}

data class UtenlandskSykmelding(
    override val id: String,
    override val metadata: SykmeldingMetadata,
    override val pasient: Pasient,
    override val medisinskVurdering: MedisinskVurdering,
    override val aktivitet: List<Aktivitet>,
    val utenlandskInfo: UtenlandskInfo,
) : Sykmelding {
    override val type = SykmeldingType.UTENLANDSK
}

data class DigitalSykmelding(
    override val id: String,
    override val metadata: DigitalSykmeldingMetadata,
    override val pasient: Pasient,
    override val medisinskVurdering: MedisinskVurdering,
    override val aktivitet: List<Aktivitet>,
    val behandler: Behandler,
    val sykmelder: Sykmelder,
    val arbeidsgiver: ArbeidsgiverInfo,
    val tilbakedatering: Tilbakedatering?,
    val bistandNav: BistandNav?,
    val utdypendeSporsmal: List<UtdypendeSporsmal>?,
) : Sykmelding {
    override val type = SykmeldingType.DIGITAL
}

data class XmlSykmelding(
    override val id: String,
    override val metadata: SykmeldingMetadata,
    override val pasient: Pasient,
    override val medisinskVurdering: MedisinskVurdering,
    override val aktivitet: List<Aktivitet>,
    val arbeidsgiver: ArbeidsgiverInfo,
    val behandler: Behandler,
    val sykmelder: Sykmelder,
    val prognose: Prognose?,
    val tiltak: Tiltak?,
    val bistandNav: BistandNav?,
    val tilbakedatering: Tilbakedatering?,
    val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>?,
) : Sykmelding {
    override val type = SykmeldingType.XML
}

data class Papirsykmelding(
    override val id: String,
    override val metadata: SykmeldingMetadata,
    override val pasient: Pasient,
    override val medisinskVurdering: MedisinskVurdering,
    override val aktivitet: List<Aktivitet>,
    val arbeidsgiver: ArbeidsgiverInfo,
    val behandler: Behandler,
    val sykmelder: Sykmelder,
    val prognose: Prognose?,
    val tiltak: Tiltak?,
    val bistandNav: BistandNav?,
    val tilbakedatering: Tilbakedatering?,
    val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>?,
) : Sykmelding {
    override val type = SykmeldingType.PAPIR
}

data class AvsenderSystem(val navn: String, val versjon: String)

sealed interface SykmeldingMeta {
    val mottattDato: OffsetDateTime
    val genDate: OffsetDateTime
    val avsenderSystem: AvsenderSystem
}

data class SykmeldingMetadata(
    override val mottattDato: OffsetDateTime,
    override val genDate: OffsetDateTime,
    override val avsenderSystem: AvsenderSystem,
    val behandletTidspunkt: OffsetDateTime,
    val regelsettVersjon: String?,
    val strekkode: String?,
) : SykmeldingMeta

data class DigitalSykmeldingMetadata(
    override val mottattDato: OffsetDateTime,
    override val genDate: OffsetDateTime,
    override val avsenderSystem: AvsenderSystem,
) : SykmeldingMeta

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
