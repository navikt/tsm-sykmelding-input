package no.nav.tsm.sykmelding.input.core.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import no.nav.tsm.sykmelding.input.core.model.metadata.Digital
import no.nav.tsm.sykmelding.input.core.model.metadata.HelsepersonellKategori
import no.nav.tsm.sykmelding.input.core.model.metadata.Navn
import org.junit.jupiter.api.Assertions.*

class SykmeldingObjectMapperTest {

    @Test
    fun `SykmeldingObjectMapper skal serialisere og deserialisert sykmelding`() {
        val metadata = Digital(orgnummer = "987654321")
        val sykmeldingRecord =
            SykmeldingRecord(
                metadata = metadata,
                validation =
                    ValidationResult(
                        status = RuleType.OK,
                        timestamp = OffsetDateTime.now(ZoneOffset.UTC),
                        emptyList(),
                    ),
                sykmelding =
                    XmlSykmelding(
                        id = UUID.randomUUID().toString(),
                        metadata =
                            SykmeldingMetadata(
                                mottattDato = OffsetDateTime.now(ZoneOffset.UTC),
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                avsenderSystem = AvsenderSystem("test", "1"),
                                behandletTidspunkt = OffsetDateTime.now(ZoneOffset.UTC),
                                regelsettVersjon = "1234",
                                strekkode = null,
                            ),
                        pasient = Pasient(null, null, null, "12345678912", emptyList()),
                        medisinskVurdering =
                            LegacyMedisinskVurdering(
                                hovedDiagnose =
                                    DiagnoseInfo(DiagnoseSystem.ICPC2B, "R74.0001", "diagnose"),
                                biDiagnoser = emptyList(),
                                svangerskap = false,
                                yrkesskade = null,
                                skjermetForPasient = false,
                                syketilfelletStartDato = null,
                                annenFraversArsak =
                                    AnnenFraverArsak(
                                        "beskrivelse",
                                        listOf(
                                            AnnenFravarArsakType
                                                .MOTTAR_TILSKUDD_GRUNNET_HELSETILSTAND
                                        ),
                                    ),
                            ),
                        aktivitet =
                            listOf(
                                AktivitetIkkeMulig(
                                    medisinskArsak = null,
                                    arbeidsrelatertArsak = null,
                                    fom = LocalDate.now(),
                                    tom = LocalDate.now(),
                                )
                            ),
                        arbeidsgiver = IngenArbeidsgiver(),
                        behandler =
                            Behandler(
                                navn =
                                    Navn(
                                        fornavn = "fornavn",
                                        mellomnavn = "mellomnavn",
                                        etternavn = "etternavn",
                                    ),
                                adresse = null,
                                ids = emptyList(),
                                kontaktinfo = emptyList(),
                            ),
                        sykmelder = Sykmelder(emptyList(), HelsepersonellKategori.LEGE),
                        prognose = null,
                        tiltak = null,
                        bistandNav = null,
                        tilbakedatering = null,
                        utdypendeOpplysninger = null,
                    ),
            )

        val serialized = sykmeldingObjectMapper.writeValueAsString(sykmeldingRecord)
        val deserialized =
            sykmeldingObjectMapper.readValue(serialized, SykmeldingRecord::class.java)

        assertEquals(sykmeldingRecord, deserialized)
    }

    @Test
    fun `Should serialize and deserialize digital sykmelding`() {
        val metadata = Digital("123456789")
        val sykmeldingRecord =
            SykmeldingRecord(
                metadata = metadata,
                validation =
                    ValidationResult(
                        status = RuleType.OK,
                        timestamp = OffsetDateTime.now(ZoneOffset.UTC),
                        emptyList(),
                    ),
                sykmelding =
                    DigitalSykmelding(
                        id = UUID.randomUUID().toString(),
                        metadata =
                            DigitalSykmeldingMetadata(
                                mottattDato = OffsetDateTime.now(ZoneOffset.UTC),
                                genDate = OffsetDateTime.now(ZoneOffset.UTC),
                                avsenderSystem = AvsenderSystem("test", "1"),
                            ),
                        pasient = Pasient(null, null, null, "12345678901", emptyList()),
                        medisinskVurdering =
                            DigitalMedisinskVurdering(
                                hovedDiagnose = null,
                                biDiagnoser = emptyList(),
                                svangerskap = false,
                                yrkesskade = null,
                                skjermetForPasient = false,
                                annenFravarsgrunn =
                                    AnnenFravarsgrunn.NODVENDIG_KONTROLLUNDENRSOKELSE,
                            ),
                        aktivitet =
                            listOf(
                                AktivitetIkkeMulig(
                                    medisinskArsak = null,
                                    arbeidsrelatertArsak = null,
                                    fom = LocalDate.now(),
                                    tom = LocalDate.now(),
                                )
                            ),
                        behandler =
                            Behandler(
                                navn = Navn("fornavn", "mellomnavn", "etternavn"),
                                adresse = null,
                                ids = emptyList(),
                                kontaktinfo = emptyList(),
                            ),
                        sykmelder = Sykmelder(emptyList(), HelsepersonellKategori.LEGE),
                        arbeidsgiver = IngenArbeidsgiver(),
                        tilbakedatering = null,
                        bistandNav = null,
                        utdypendeSporsmal = null,
                    ),
            )
        val serialized = sykmeldingObjectMapper.writeValueAsString(sykmeldingRecord)
        val deserialized =
            sykmeldingObjectMapper.readValue(serialized, SykmeldingRecord::class.java)
        assertEquals(sykmeldingRecord, deserialized)
    }
}
