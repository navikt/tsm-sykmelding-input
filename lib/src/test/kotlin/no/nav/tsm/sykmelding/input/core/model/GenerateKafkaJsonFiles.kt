package no.nav.tsm.sykmelding.input.core.model

import com.fasterxml.jackson.databind.SerializationFeature
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import no.nav.tsm.sykmelding.input.core.model.metadata.Ack
import no.nav.tsm.sykmelding.input.core.model.metadata.AckType
import no.nav.tsm.sykmelding.input.core.model.metadata.Adresse
import no.nav.tsm.sykmelding.input.core.model.metadata.AdresseType
import no.nav.tsm.sykmelding.input.core.model.metadata.HelsepersonellKategori
import no.nav.tsm.sykmelding.input.core.model.metadata.Kjonn
import no.nav.tsm.sykmelding.input.core.model.metadata.Kontaktinfo
import no.nav.tsm.sykmelding.input.core.model.metadata.KontaktinfoType
import no.nav.tsm.sykmelding.input.core.model.metadata.Meldingstype
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageInfo
import no.nav.tsm.sykmelding.input.core.model.metadata.MessageMetadata
import no.nav.tsm.sykmelding.input.core.model.metadata.MottakenhetBlokk
import no.nav.tsm.sykmelding.input.core.model.metadata.Navn
import no.nav.tsm.sykmelding.input.core.model.metadata.OrgId
import no.nav.tsm.sykmelding.input.core.model.metadata.OrgIdType
import no.nav.tsm.sykmelding.input.core.model.metadata.Organisasjon
import no.nav.tsm.sykmelding.input.core.model.metadata.OrganisasjonsType
import no.nav.tsm.sykmelding.input.core.model.metadata.Pasient as MetadataPasient
import no.nav.tsm.sykmelding.input.core.model.metadata.PersonId
import no.nav.tsm.sykmelding.input.core.model.metadata.PersonIdType

fun main() {
    val version =
        System.getProperty("version")
            ?: error(
                "version system property not set. Run via: ./gradlew :lib:generateKafkaJsonFiles"
            )
    val outputDir = Path.of("lib/src/test/resources/format/v$version").toAbsolutePath()
    Files.createDirectories(outputDir)
    println("Writing fixtures (version=$version) to: $outputDir")

    val mapper = sykmeldingObjectMapper.copy().enable(SerializationFeature.INDENT_OUTPUT)

    val sykmeldingerJsons =
        linkedMapOf(
            "digital-full.json" to digitalFull(),
            "digital-min.json" to digitalMin(),
            "xml-egenmeldt-full.json" to xmlEgenmeldtFull(),
            "xml-egenmeldt-min.json" to xmlEgenmeldtMin(),
            "xml-emottak-edi-full.json" to xmlEmottakEdiFull(),
            "xml-emottak-edi-min.json" to xmlEmottakEdiMin(),
            "xml-emottak-legacy-full.json" to xmlEmottakLegacyFull(),
            "xml-emottak-legacy-min.json" to xmlEmottakLegacyMin(),
            "papir-full.json" to papirFull(),
            "papir-min.json" to papirMin(),
            "utenlandsk-full.json" to utenlandskFull(),
            "utenlandsk-min.json" to utenlandskMin(),
        )

    sykmeldingerJsons.forEach { (name, record) ->
        val path = outputDir.resolve(name)
        val json = mapper.writeValueAsString(record)
        Files.writeString(path, json + "\n")
    }
}

private val mottattDato = OffsetDateTime.parse("2024-01-15T10:00:00Z")
private val genDate = OffsetDateTime.parse("2024-01-15T09:00:00Z")
private val behandletTidspunkt = OffsetDateTime.parse("2024-01-15T09:30:00Z")
private val validationTime = OffsetDateTime.parse("2024-01-15T10:01:00Z")
private val fom = LocalDate.parse("2024-01-15")
private val tom = LocalDate.parse("2024-01-29")

private fun digitalFull() =
    SykmeldingRecord.Digital(
        metadata = MessageMetadata.Digital(orgnummer = "987654321"),
        validation = fullValidation(),
        sykmelding =
            Sykmelding.Digital(
                id = UUID.randomUUID().toString(),
                metadata = digitalSykmeldingMetadata(),
                pasient = pasientFull(),
                medisinskVurdering = medisinskVurderingDigital(),
                aktivitet = aktivitetVariety(),
                behandler = behandlerFull(),
                sykmelder = sykmelderFull(),
                arbeidsgiver = ArbeidsgiverInfo.Flere(
                        "navn",
                        "yrke",
                        20,
                        "melding til arbeidsgiver",
                        "tiltak abreidsplassen",
                    ),
                tilbakedatering = tilbakedatering(),
                bistandNav = bistandNav(),
                utdypendeSporsmal =
                    listOf(
                        UtdypendeSporsmal(
                            svar = "Medisinsk oppsumering",
                            type = Sporsmalstype.MEDISINSK_OPPSUMMERING,
                            skjermetForArbeidsgiver = true,
                            sporsmal = "Hva er utfordringene?",
                        ),
                        UtdypendeSporsmal(
                            svar = "Pasient har utfordringer",
                            type = Sporsmalstype.UTFORDRINGER_MED_GRADERT_ARBEID,
                            skjermetForArbeidsgiver = true,
                            sporsmal = "Hva er utfordringene?",
                        ),
                        UtdypendeSporsmal(
                            svar = "Hensyn",
                            type = Sporsmalstype.HENSYN_PA_ARBEIDSPLASSEN,
                            skjermetForArbeidsgiver = true,
                            sporsmal = "Hva er utfordringene?",
                        ),
                    ),
            ),
    )

private fun digitalMin() =
    SykmeldingRecord.Digital(
        metadata = MessageMetadata.Digital(orgnummer = "100000000"),
        validation = okValidationEmpty(),
        sykmelding =
            Sykmelding.Digital(
                id = UUID.randomUUID().toString(),
                metadata = digitalSykmeldingMetadata(),
                pasient = pasientMin(),
                medisinskVurdering = digitalMedisinskVurderingMin(),
                aktivitet = listOf(aktivitetIkkeMuligMin()),
                behandler = behandlerMin(),
                sykmelder = sykmelderMin(),
                arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                tilbakedatering = null,
                bistandNav = null,
                utdypendeSporsmal = null,
            ),
    )

private fun xmlEgenmeldtFull() =
    SykmeldingRecord.Xml(
        metadata = MessageMetadata.Xml.Egenmeldt(msgInfo = msgInfoFull()),
        validation = fullValidation(),
        sykmelding = xmlSykmeldingFull(),
    )

private fun xmlEgenmeldtMin() =
    SykmeldingRecord.Xml(
        metadata = MessageMetadata.Xml.Egenmeldt(msgInfo = msgInfoMin()),
        validation = okValidationEmpty(),
        sykmelding = xmlSykmeldingMin("22222222-0000-0000-0000-000000000002"),
    )

private fun xmlEmottakEdiFull() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.EDI(
                mottakenhetBlokk = mottakenhetBlokkFull(),
                ack = Ack(AckType.JA),
                msgInfo = msgInfoFull(),
                sender = organisasjonFull("Sender Org"),
                receiver = organisasjonFull("Receiver Org"),
                pasient = metadataPasientFull(),
                vedlegg = listOf("vedlegg-1.pdf", "vedlegg-2.pdf"),
            ),
        validation = fullValidation(),
        sykmelding = xmlSykmeldingFull(),
    )

private fun xmlEmottakEdiMin() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.EDI(
                mottakenhetBlokk = mottakenhetBlokkMin(),
                ack = Ack(AckType.IKKE_OPPGITT),
                msgInfo = msgInfoMin(),
                sender = organisasjonMin("Sender Org"),
                receiver = organisasjonMin("Receiver Org"),
                pasient = null,
                vedlegg = null,
            ),
        validation = okValidationEmpty(),
        sykmelding = xmlSykmeldingMin("33333333-0000-0000-0000-000000000003"),
    )

private fun xmlEmottakLegacyFull() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.Legacy(
                msgInfo = msgInfoFull(),
                sender = organisasjonFull("Sender Org"),
                receiver = organisasjonFull("Receiver Org"),
                vedlegg = listOf("attached.pdf"),
            ),
        validation = fullValidation(),
        sykmelding = xmlSykmeldingFull(),
    )

private fun xmlEmottakLegacyMin() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.Legacy(
                msgInfo = msgInfoMin(),
                sender = organisasjonMin("Sender Org"),
                receiver = organisasjonMin("Receiver Org"),
                vedlegg = null,
            ),
        validation = okValidationEmpty(),
        sykmelding = xmlSykmeldingMin("44444444-0000-0000-0000-000000000004"),
    )

private fun papirFull() =
    SykmeldingRecord.Papir(
        metadata =
            MessageMetadata.Papir(
                msgInfo = msgInfoFull(),
                sender = organisasjonFull("Sender Org"),
                receiver = organisasjonFull("Receiver Org"),
                journalPostId = "journal-papir-full",
            ),
        validation = fullValidation(),
        sykmelding =
            Sykmelding.Papir(
                id = "55555555-5555-5555-5555-555555555555",
                metadata = sykmeldingMetadataFull(),
                pasient = pasientFull(),
                medisinskVurdering = legacyMedisinskVurderingFull(),
                aktivitet = aktivitetVariety(),
                arbeidsgiver = ArbeidsgiverInfo.Flere(
                        "navn",
                        "yrke",
                        20,
                        "melding til arbeidsgiver",
                        "tiltak abreidsplassen",
                    ),
                behandler = behandlerFull(),
                sykmelder = sykmelderFull(),
                prognose = prognoseFull(),
                tiltak = Tiltak(tiltakNav = "Veiledning", andreTiltak = "Ergonomi"),
                bistandNav =
                    BistandNav(bistandUmiddelbart = false, beskrivBistand = "Ikke nødvendig"),
                tilbakedatering =
                    Tilbakedatering(
                        kontaktDato = LocalDate.parse("2024-01-12"),
                        begrunnelse = "Sen registrering",
                    ),
                utdypendeOpplysninger = utdypendeOpplysningerFull(),
            ),
    )

private fun papirMin() =
    SykmeldingRecord.Papir(
        metadata =
            MessageMetadata.Papir(
                msgInfo = msgInfoMin(),
                sender = organisasjonMin("Sender Org"),
                receiver = organisasjonMin("Receiver Org"),
                journalPostId = "journal-papir-min",
            ),
        validation = okValidationEmpty(),
        sykmelding =
            Sykmelding.Papir(
                id = UUID.randomUUID().toString(),
                metadata = sykmeldingMetadataMin(),
                pasient = pasientMin(),
                medisinskVurdering = legacyMedisinskVurderingMin(),
                aktivitet = listOf(aktivitetIkkeMuligMin()),
                arbeidsgiver = ArbeidsgiverInfo.Ingen(),
                behandler = behandlerMin(),
                sykmelder = sykmelderMin(),
                prognose = null,
                tiltak = null,
                bistandNav = null,
                tilbakedatering = null,
                utdypendeOpplysninger = null,
            ),
    )

private fun msgInfoFull(): MessageInfo =
    MessageInfo(
        type = Meldingstype.SYKMELDING,
        genDate = genDate,
        msgId = "msg-id",
        migVersjon = "v1",
    )

private fun utenlandskFull() =
    SykmeldingRecord.Utenlandsk(
        metadata = MessageMetadata.Utenlandsk(land = "SE", journalPostId = "journal-utenlandsk-full"),
        validation = fullValidation(),
        sykmelding =
            Sykmelding.Utenlandsk(
                id = UUID.randomUUID().toString(),
                metadata = sykmeldingMetadataFull(),
                pasient = pasientFull(),
                medisinskVurdering = legacyMedisinskVurderingFull(),
                aktivitet = aktivitetVariety(),
                utenlandskInfo =
                    UtenlandskInfo(
                        land = "SE",
                        folkeRegistertAdresseErBrakkeEllerTilsvarende = true,
                        erAdresseUtland = true,
                    ),
            ),
    )

private fun msgInfoMin(): MessageInfo =
    MessageInfo(
        type = Meldingstype.SYKMELDING,
        genDate = genDate,
        msgId = "msg-id",
        migVersjon = null,
    )

private fun utenlandskMin() =
    SykmeldingRecord.Utenlandsk(
        metadata = MessageMetadata.Utenlandsk(land = "DK", journalPostId = "journal-utenlandsk-min"),
        validation = okValidationEmpty(),
        sykmelding =
            Sykmelding.Utenlandsk(
                id = UUID.randomUUID().toString(),
                metadata = sykmeldingMetadataMin(),
                pasient = pasientMin(),
                medisinskVurdering = legacyMedisinskVurderingMin(),
                aktivitet = listOf(aktivitetIkkeMuligMin()),
                utenlandskInfo =
                    UtenlandskInfo(
                        land = "DK",
                        folkeRegistertAdresseErBrakkeEllerTilsvarende = false,
                        erAdresseUtland = null,
                    ),
            ),
    )

private fun xmlSykmeldingFull() =
    Sykmelding.Xml(
        id = UUID.randomUUID().toString(),
        metadata = sykmeldingMetadataFull(),
        pasient = pasientFull(),
        medisinskVurdering = legacyMedisinskVurderingFull(),
        aktivitet = aktivitetVariety(),
        arbeidsgiver = enArbeidsgiverFull(),
        behandler = behandlerFull(),
        sykmelder = sykmelderFull(),
        prognose = prognoseFull(),
        tiltak = tiltak(),
        bistandNav = bistandNav(),
        tilbakedatering = tilbakedatering(),
        utdypendeOpplysninger = utdypendeOpplysningerFull(),
    )

private fun tiltak(): Tiltak = Tiltak(tiltakNav = "Tiltak Nav", andreTiltak = "Andre tiltak")

private fun xmlSykmeldingMin(id: String) =
    Sykmelding.Xml(
        id = UUID.randomUUID().toString(),
        metadata = sykmeldingMetadataMin(),
        pasient = pasientMin(),
        medisinskVurdering = legacyMedisinskVurderingMin(),
        aktivitet = listOf(aktivitetIkkeMuligMin()),
        arbeidsgiver = ArbeidsgiverInfo.Ingen(),
        behandler = behandlerMin(),
        sykmelder = sykmelderMin(),
        prognose = null,
        tiltak = null,
        bistandNav = null,
        tilbakedatering = null,
        utdypendeOpplysninger = null,
    )

private fun aktivitetVariety() =
    listOf(
        Aktivitet.IkkeMulig(
            medisinskArsak =
                MedisinskArsak(
                    beskrivelse = "Tilstand hindrer arbeid",
                    arsak = listOf(MedisinskArsakType.TILSTAND_HINDRER_AKTIVITET),
                ),
            arbeidsrelatertArsak =
                ArbeidsrelatertArsak(
                    beskrivelse = "Manglende tilrettelegging",
                    arsak = listOf(ArbeidsrelatertArsakType.MANGLENDE_TILRETTELEGGING),
                ),
            fom = fom,
            tom = fom.plusDays(7),
        ),
        Aktivitet.Gradert(grad = 50, fom = fom.plusDays(8), tom = fom.plusDays(14), reisetilskudd = false),
        Aktivitet.Avventende(
            innspillTilArbeidsgiver = "Snakk med leder",
            fom = fom.plusDays(15),
            tom = fom.plusDays(20),
        ),
        Aktivitet.Behandlingsdager(
            antallBehandlingsdager = 3,
            fom = fom.plusDays(21),
            tom = fom.plusDays(23),
        ),
        Aktivitet.Reisetilskudd(fom = fom.plusDays(24), tom = tom),
    )

private fun aktivitetIkkeMuligMin() =
    Aktivitet.IkkeMulig(medisinskArsak = null, arbeidsrelatertArsak = null, fom = fom, tom = tom)

private fun pasientFull() =
    Pasient(
        navn = Navn("Ola", "Mellom", "Nordmann"),
        navKontor = "NAV Oslo",
        navnFastlege = "Dr. Hansen",
        fnr = "12345678901",
        kontaktinfo =
            listOf(
                Kontaktinfo(KontaktinfoType.MOBILTELEFON, "+4799999999"),
                Kontaktinfo(KontaktinfoType.HOVEDTELEFON, "+4722222222"),
            ),
    )

private fun pasientMin() = Pasient(null, null, null, "12345678901", emptyList())

private fun behandlerFull() =
    Behandler(
        navn = Navn("Lege", "Mellom", "Legesen"),
        adresse =
            Adresse(
                type = AdresseType.ARBEIDSADRESSE,
                gateadresse = "Gata 1",
                postnummer = "0001",
                poststed = "Oslo",
                postboks = "boxs",
                kommune = "Oslo",
                land = "Norge",
            ),
        ids =
            listOf(PersonId("12345678901", PersonIdType.FNR), PersonId("987654", PersonIdType.HPR)),
        kontaktinfo = listOf(Kontaktinfo(KontaktinfoType.ARBEIDSPLASS, "+4733333333")),
    )

private fun behandlerMin() =
    Behandler(
        navn = Navn("fornavn", null, "etternavn"),
        adresse = null,
        ids = emptyList(),
        kontaktinfo = emptyList(),
    )

private fun sykmelderFull() =
    Sykmelder(
        ids =
            listOf(PersonId("12345678901", PersonIdType.FNR), PersonId("987654", PersonIdType.HPR)),
        helsepersonellKategori = HelsepersonellKategori.LEGE,
    )

private fun sykmelderMin() = Sykmelder(emptyList(), HelsepersonellKategori.LEGE)

private fun enArbeidsgiverFull() =
    ArbeidsgiverInfo.En(
        navn = "ACME AS",
        yrkesbetegnelse = "Utvikler",
        stillingsprosent = 100,
        meldingTilArbeidsgiver = "Vennligst tilrettelegg",
        tiltakArbeidsplassen = "Hev/senk-pult",
    )

private fun sykmeldingMetadataFull() =
    SykmeldingMeta.Legacy(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("avsender-app", "2.5"),
        behandletTidspunkt = behandletTidspunkt,
        regelsettVersjon = "1234",
        strekkode = "1234567890",
    )

private fun sykmeldingMetadataMin() =
    SykmeldingMeta.Legacy(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("min", "1"),
        behandletTidspunkt = behandletTidspunkt,
        regelsettVersjon = null,
        strekkode = null,
    )

private fun legacyMedisinskVurderingFull() =
    MedisinskVurdering.Legacy(
        hovedDiagnose = DiagnoseInfo(DiagnoseSystem.ICPC2B, "R74.0001", "diagnose"),
        biDiagnoser = bidiagnoser(),
        svangerskap = false,
        yrkesskade = Yrkesskade(yrkesskadeDato = LocalDate.parse("2023-08-01")),
        skjermetForPasient = false,
        syketilfelletStartDato = LocalDate.parse("2024-01-10"),
        annenFraversArsak =
            AnnenFraverArsak(
                beskrivelse = "Beskrivelse av fravær",
                arsak = listOf(AnnenFravarArsakType.MOTTAR_TILSKUDD_GRUNNET_HELSETILSTAND),
            ),
    )

private fun bidiagnoser(): List<DiagnoseInfo> =
    listOf(
        DiagnoseInfo(DiagnoseSystem.ICD10, "J06.9", "URI"),
        DiagnoseInfo(DiagnoseSystem.ICPC2, "L84", "Rygglidelse"),
    )

private fun legacyMedisinskVurderingMin() =
    MedisinskVurdering.Legacy(
        hovedDiagnose = null,
        biDiagnoser = emptyList(),
        svangerskap = false,
        yrkesskade = null,
        skjermetForPasient = false,
        syketilfelletStartDato = null,
        annenFraversArsak = null,
    )

private fun digitalMedisinskVurderingMin() =
    MedisinskVurdering.Digital(
        hovedDiagnose = null,
        biDiagnoser = emptyList(),
        svangerskap = false,
        yrkesskade = null,
        skjermetForPasient = false,
        annenFravarsgrunn = null,
    )

private fun prognoseFull() =
    Prognose(
        arbeidsforEtterPeriode = true,
        hensynArbeidsplassen = "hensyn",
        arbeid =
            IArbeid.ErIArbeid(
                egetArbeidPaSikt = true,
                annetArbeidPaSikt = false,
                arbeidFOM = LocalDate.parse("2024-02-01"),
                vurderingsdato = LocalDate.parse("2024-01-20"),
            ),
    )

private fun utdypendeOpplysningerFull() =
    mapOf(
        "6.2" to
            mapOf(
                "6.2.1" to
                    SporsmalSvar(
                        sporsmal = "Hva er den medisinske årsaken?",
                        svar = "Beskrivelse",
                        restriksjoner = listOf(SvarRestriksjon.SKJERMET_FOR_ARBEIDSGIVER),
                    )
            )
    )

private fun mottakenhetBlokkFull() =
    MottakenhetBlokk(
        ediLogid = "edi-log-1",
        avsender = "avsender-fnr",
        ebXMLSamtaleId = "samtale-1",
        mottaksId = "mottaks-1",
        meldingsType = "SYKMELD",
        avsenderRef = "ref-1",
        avsenderFnrFraDigSignatur = "12345678901",
        mottattDato = mottattDato,
        orgnummer = "987654321",
        avsenderOrgNrFraDigSignatur = "987654321",
        partnerReferanse = "partner-1",
        herIdentifikator = "her-1",
        ebRole = "Sykmelder",
        ebService = "Sykmelding",
        ebAction = "Send",
    )

private fun mottakenhetBlokkMin() =
    MottakenhetBlokk(
        ediLogid = "edi-log-min",
        avsender = "avsender-min",
        ebXMLSamtaleId = "samtale-min",
        mottaksId = null,
        meldingsType = "SYKMELD",
        avsenderRef = "ref-min",
        avsenderFnrFraDigSignatur = null,
        mottattDato = mottattDato,
        orgnummer = null,
        avsenderOrgNrFraDigSignatur = null,
        partnerReferanse = "partner-min",
        herIdentifikator = "her-min",
        ebRole = "Sykmelder",
        ebService = "Sykmelding",
        ebAction = "Send",
    )

private fun organisasjonFull(navn: String) =
    Organisasjon(
        navn = navn,
        type = OrganisasjonsType.PRIVATE_SPESIALISTER_MED_DRIFTSAVTALER,
        ids = listOf(OrgId("987654321", OrgIdType.ENH)),
        adresse =
            Adresse(
                type = AdresseType.BESOKSADRESSE,
                gateadresse = "Gata 2",
                postnummer = "0002",
                poststed = "Oslo",
                postboks = null,
                kommune = "Oslo",
                land = "Norge",
            ),
        kontaktinfo = listOf(Kontaktinfo(KontaktinfoType.ARBEIDSPLASS_SENTRALBORD, "+4744444444")),
        underOrganisasjon = null,
        helsepersonell = null,
    )

private fun organisasjonMin(navn: String) =
    Organisasjon(
        navn = navn,
        type = OrganisasjonsType.IKKE_OPPGITT,
        ids = emptyList(),
        adresse = null,
        kontaktinfo = emptyList(),
        underOrganisasjon = null,
        helsepersonell = null,
    )

private fun metadataPasientFull() =
    MetadataPasient(
        ids = listOf(PersonId("12345678901", PersonIdType.FNR)),
        navn = Navn("Ola", null, "Nordmann"),
        fodselsdato = LocalDate.parse("1990-05-15"),
        kjonn = Kjonn.MANN,
        nasjonalitet = "NO",
        adresse = null,
        kontaktinfo = emptyList(),
    )

private fun fullValidation() =
    ValidationResult(
        status = RuleType.OK,
        timestamp = validationTime,
        rules =
            listOf(
                Rule.OK(
                    name = "RULE_OK_1",
                    timestamp = validationTime,
                    validationType = ValidationType.AUTOMATIC,
                ),
                Rule.Pending(
                    name = "RULE_PENDING_1",
                    timestamp = validationTime,
                    validationType = ValidationType.MANUAL,
                    reason = Reason(sykmeldt = "Avventer", sykmelder = "Avventer behandler"),
                ),
            ),
    )

private fun okValidationEmpty() =
    ValidationResult(status = RuleType.OK, timestamp = validationTime, rules = emptyList())

private fun bistandNav(): BistandNav =
    BistandNav(bistandUmiddelbart = true, beskrivBistand = "Trenger oppfølging")

private fun tilbakedatering(): Tilbakedatering =
    Tilbakedatering(
        kontaktDato = LocalDate.parse("2024-01-10"),
        begrunnelse = "Pasient kunne ikke møte tidligere",
    )

private fun medisinskVurderingDigital(): MedisinskVurdering.Digital =
    MedisinskVurdering.Digital(
        hovedDiagnose = hovedDiagnose(),
        biDiagnoser = bidiagnoser(),
        svangerskap = true,
        yrkesskade = Yrkesskade(yrkesskadeDato = LocalDate.parse("2023-08-01")),
        skjermetForPasient = false,
        annenFravarsgrunn = AnnenFravarsgrunn.NODVENDIG_KONTROLLUNDENRSOKELSE,
    )

private fun digitalSykmeldingMetadata(): SykmeldingMeta.Digital =
    SykmeldingMeta.Digital(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("avsender-app", "2.5"),
    )

private fun hovedDiagnose(): DiagnoseInfo =
    DiagnoseInfo(DiagnoseSystem.ICPC2B, "R74.0001", "diagnose-tekst")
