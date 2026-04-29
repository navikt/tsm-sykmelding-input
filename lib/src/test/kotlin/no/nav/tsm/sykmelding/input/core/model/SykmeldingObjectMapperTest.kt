package no.nav.tsm.sykmelding.input.core.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import no.nav.tsm.sykmelding.input.core.model.metadata.HelsepersonellKategori
import no.nav.tsm.sykmelding.input.core.model.metadata.Meldingstype
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageInfo
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageMetadata
import no.nav.tsm.sykmelding.input.core.model.metadata.Navn
import no.nav.tsm.sykmelding.input.core.model.metadata.Organisasjon
import no.nav.tsm.sykmelding.input.core.model.metadata.OrganisasjonsType
import org.junit.jupiter.api.Assertions.*

class SykmeldingObjectMapperTest {

    @Test
    fun `round trips SykmeldingRecord_Xml`() {
        val record =
            SykmeldingRecord.Xml(
                metadata =
                    MessageMetadata.Xml.Egenmeldt(
                        msgInfo =
                            MessageInfo(
                                type = Meldingstype.SYKMELDING,
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                msgId = UUID.randomUUID().toString(),
                                migVersjon = null,
                            )
                    ),
                validation = okValidation(),
                sykmelding =
                    Sykmelding.Xml(
                        id = UUID.randomUUID().toString(),
                        metadata = legacySykmeldingMetadata(),
                        pasient = pasient(),
                        medisinskVurdering = legacyMedisinskVurdering(),
                        aktivitet = listOf(aktivitetIkkeMulig()),
                        arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                        behandler = behandler(),
                        sykmelder = sykmelder(),
                        prognose = null,
                        tiltak = null,
                        bistandNav = null,
                        tilbakedatering = null,
                        utdypendeOpplysninger = null,
                    ),
            )

        assertRoundTrip(record)
    }

    @Test
    fun `round trips SykmeldingRecord_Digital`() {
        val record =
            SykmeldingRecord.Digital(
                metadata = MessageMetadata.Digital(orgnummer = "123456789"),
                validation = okValidation(),
                sykmelding =
                    Sykmelding.Digital(
                        id = UUID.randomUUID().toString(),
                        metadata =
                            SykmeldingMeta.Digital(
                                mottattDato = OffsetDateTime.now(ZoneOffset.UTC),
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                avsenderSystem = AvsenderSystem("test", "1"),
                            ),
                        pasient = pasient(),
                        medisinskVurdering =
                            MedisinskVurdering.Digital(
                                hovedDiagnose = null,
                                biDiagnoser = emptyList(),
                                svangerskap = false,
                                yrkesskade = null,
                                skjermetForPasient = false,
                                annenFravarsgrunn =
                                    AnnenFravarsgrunn.NODVENDIG_KONTROLLUNDENRSOKELSE,
                            ),
                        aktivitet = listOf(aktivitetIkkeMulig()),
                        behandler = behandler(),
                        sykmelder = sykmelder(),
                        arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                        tilbakedatering = null,
                        bistandNav = null,
                        utdypendeSporsmal = null,
                    ),
            )

        assertRoundTrip(record)
    }

    @Test
    fun `round trips SykmeldingRecord_Papir`() {
        val record =
            SykmeldingRecord.Papir(
                metadata =
                    MessageMetadata.Papir(
                        msgInfo =
                            MessageInfo(
                                type = Meldingstype.SYKMELDING,
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                msgId = UUID.randomUUID().toString(),
                                migVersjon = null,
                            ),
                        sender = organisasjon(),
                        receiver = organisasjon(),
                        journalPostId = "journal-1",
                    ),
                validation = okValidation(),
                sykmelding =
                    Sykmelding.Papir(
                        id = UUID.randomUUID().toString(),
                        metadata = legacySykmeldingMetadata(),
                        pasient = pasient(),
                        medisinskVurdering = legacyMedisinskVurdering(),
                        aktivitet = listOf(aktivitetIkkeMulig()),
                        arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                        behandler = behandler(),
                        sykmelder = sykmelder(),
                        prognose = null,
                        tiltak = null,
                        bistandNav = null,
                        tilbakedatering = null,
                        utdypendeOpplysninger = null,
                    ),
            )

        assertRoundTrip(record)
    }

    @Test
    fun `round trips SykmeldingRecord_Utenlandsk`() {
        val record =
            SykmeldingRecord.Utenlandsk(
                metadata = MessageMetadata.Utenlandsk(land = "SE", journalPostId = "journal-2"),
                validation = okValidation(),
                sykmelding =
                    Sykmelding.Utenlandsk(
                        id = UUID.randomUUID().toString(),
                        metadata = legacySykmeldingMetadata(),
                        pasient = pasient(),
                        medisinskVurdering = legacyMedisinskVurdering(),
                        aktivitet = listOf(aktivitetIkkeMulig()),
                        utenlandskInfo =
                            UtenlandskInfo(
                                land = "SE",
                                folkeRegistertAdresseErBrakkeEllerTilsvarende = false,
                                erAdresseUtland = null,
                            ),
                    ),
            )

        assertRoundTrip(record)
    }

    @Test
    fun `dispatches to the right subtype when reading the sealed parent`() {
        val record =
            SykmeldingRecord.Digital(
                metadata = MessageMetadata.Digital(orgnummer = "123456789"),
                validation = okValidation(),
                sykmelding =
                    Sykmelding.Digital(
                        id = UUID.randomUUID().toString(),
                        metadata =
                            SykmeldingMeta.Digital(
                                mottattDato = OffsetDateTime.now(ZoneOffset.UTC),
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                avsenderSystem = AvsenderSystem("test", "1"),
                            ),
                        pasient = pasient(),
                        medisinskVurdering =
                            MedisinskVurdering.Digital(
                                hovedDiagnose = null,
                                biDiagnoser = emptyList(),
                                svangerskap = false,
                                yrkesskade = null,
                                skjermetForPasient = false,
                                annenFravarsgrunn = null,
                            ),
                        aktivitet = listOf(aktivitetIkkeMulig()),
                        behandler = behandler(),
                        sykmelder = sykmelder(),
                        arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                        tilbakedatering = null,
                        bistandNav = null,
                        utdypendeSporsmal = null,
                    ),
            )

        val serialized = sykmeldingObjectMapper.writeValueAsString(record)
        val deserialized =
            sykmeldingObjectMapper.readValue(serialized, SykmeldingRecord::class.java)

        assertTrue(deserialized is SykmeldingRecord.Digital)
        assertEquals(record, deserialized)
    }

    private fun assertRoundTrip(record: SykmeldingRecord) {
        val serialized = sykmeldingObjectMapper.writeValueAsString(record)
        val deserialized = sykmeldingObjectMapper.readValue(serialized, record::class.java)
        assertEquals(record, deserialized)
    }

    private fun okValidation() =
        ValidationResult(
            status = RuleType.OK,
            timestamp = OffsetDateTime.now(ZoneOffset.UTC),
            rules = emptyList(),
        )

    private fun pasient() = Pasient(null, null, null, "12345678901", emptyList())

    private fun behandler() =
        Behandler(
            navn = Navn("fornavn", "mellomnavn", "etternavn"),
            adresse = null,
            ids = emptyList(),
            kontaktinfo = emptyList(),
        )

    private fun sykmelder() = Sykmelder(emptyList(), HelsepersonellKategori.LEGE)

    private fun aktivitetIkkeMulig() =
        Aktivitet.IkkeMulig(
            medisinskArsak = null,
            arbeidsrelatertArsak = null,
            fom = LocalDate.now(),
            tom = LocalDate.now(),
        )

    private fun legacySykmeldingMetadata() =
        SykmeldingMeta.Legacy(
            mottattDato = OffsetDateTime.now(ZoneOffset.UTC),
            genDate = OffsetDateTime.now(ZoneOffset.UTC),
            avsenderSystem = AvsenderSystem("test", "1"),
            behandletTidspunkt = OffsetDateTime.now(ZoneOffset.UTC),
            regelsettVersjon = "1234",
            strekkode = null,
        )

    private fun legacyMedisinskVurdering() =
        MedisinskVurdering.Legacy(
            hovedDiagnose = DiagnoseInfo(DiagnoseSystem.ICPC2B, "R74.0001", "diagnose"),
            biDiagnoser = emptyList(),
            svangerskap = false,
            yrkesskade = null,
            skjermetForPasient = false,
            syketilfelletStartDato = null,
            annenFraversArsak =
                AnnenFraverArsak(
                    "beskrivelse",
                    listOf(AnnenFravarArsakType.MOTTAR_TILSKUDD_GRUNNET_HELSETILSTAND),
                ),
        )

    private fun organisasjon() =
        Organisasjon(
            navn = "Org",
            type = OrganisasjonsType.IKKE_OPPGITT,
            ids = emptyList(),
            adresse = null,
            kontaktinfo = emptyList(),
            underOrganisasjon = null,
            helsepersonell = null,
        )
}
