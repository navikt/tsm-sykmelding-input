package no.nav.tsm.sykmelding.input.core.model

import java.time.LocalDate
import java.time.OffsetDateTime
import no.nav.tsm.sykmelding.input.core.model.metadata.*

internal fun kafkaJsonSamples(): Map<String, SykmeldingRecord> =
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

internal val mottattDato = OffsetDateTime.parse("2024-01-15T10:00:00Z")
internal val genDate = OffsetDateTime.parse("2024-01-15T09:00:00Z")
internal val behandletTidspunkt = OffsetDateTime.parse("2024-01-15T09:30:00Z")
internal val validationTime = OffsetDateTime.parse("2024-01-15T10:01:00Z")
internal val fom = LocalDate.parse("2024-01-15")
internal val tom = LocalDate.parse("2024-01-29")

internal fun digitalFull() =
    SykmeldingRecord.Digital(
        metadata = MessageMetadata.Digital(orgnummer = "987654321"),
        validation = fullValidation(),
        sykmelding =
            Sykmelding.Digital(
                id = "cc79a776-d88a-45b0-84ec-6daeec283619",
                metadata = digitalSykmeldingMetadata(),
                pasient = pasientFull(),
                medisinskVurdering = medisinskVurderingDigital(),
                aktivitet = aktivitetVariety(),
                behandler = behandlerFull(),
                sykmelder = sykmelderFull(),
                arbeidsgiver =
                    ArbeidsgiverInfo.Flere(
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

internal fun digitalMin() =
    SykmeldingRecord.Digital(
        metadata = MessageMetadata.Digital(orgnummer = "100000000"),
        validation = okValidationEmpty(),
        sykmelding =
            Sykmelding.Digital(
                id = "84a936a4-60b5-4fc1-a606-8548c637ecfa",
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

internal fun xmlEgenmeldtFull() =
    SykmeldingRecord.Xml(
        metadata = MessageMetadata.Xml.Egenmeldt(msgInfo = msgInfoFull()),
        validation = fullValidation(),
        sykmelding = xmlSykmeldingFull(id = "c97848b5-f4a3-4343-86d8-48b0cdb4152c"),
    )

internal fun xmlEgenmeldtMin() =
    SykmeldingRecord.Xml(
        metadata = MessageMetadata.Xml.Egenmeldt(msgInfo = msgInfoMin()),
        validation = okValidationEmpty(),
        sykmelding = xmlSykmeldingMin(id = "1b885199-0f27-4ef1-959c-624ada3ac5c6"),
    )

internal fun xmlEmottakEdiFull() =
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
        sykmelding = xmlSykmeldingFull(id = "f7ddff74-4eb0-4972-b7ab-52efbfd02450"),
    )

internal fun xmlEmottakEdiMin() =
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
        sykmelding = xmlSykmeldingMin(id = "f9817e88-5fc7-4528-9ce1-da4724ef5bf2"),
    )

internal fun xmlEmottakLegacyFull() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.Legacy(
                msgInfo = msgInfoFull(),
                sender = organisasjonFull("Sender Org"),
                receiver = organisasjonFull("Receiver Org"),
                vedlegg = listOf("attached.pdf"),
            ),
        validation = fullValidation(),
        sykmelding = xmlSykmeldingFull(id = "6e30cc0e-abec-441f-98ee-92c0ae7ddc45"),
    )

internal fun xmlEmottakLegacyMin() =
    SykmeldingRecord.Xml(
        metadata =
            MessageMetadata.Xml.Emottak.Legacy(
                msgInfo = msgInfoMin(),
                sender = organisasjonMin("Sender Org"),
                receiver = organisasjonMin("Receiver Org"),
                vedlegg = null,
            ),
        validation = okValidationEmpty(),
        sykmelding = xmlSykmeldingMin(id = "695ae5ee-7a79-4485-b67b-d4881d74865d"),
    )

internal fun papirFull() =
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
                arbeidsgiver =
                    ArbeidsgiverInfo.Flere(
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

internal fun papirMin() =
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
                id = "33f5223c-0bfc-4690-bc39-2451618a6a88",
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

internal fun utenlandskFull() =
    SykmeldingRecord.Utenlandsk(
        metadata =
            MessageMetadata.Utenlandsk(land = "SE", journalPostId = "journal-utenlandsk-full"),
        validation = fullValidation(),
        sykmelding =
            Sykmelding.Utenlandsk(
                id = "7f48ccda-5dfb-489f-92b7-09174232c96d",
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

internal fun utenlandskMin() =
    SykmeldingRecord.Utenlandsk(
        metadata =
            MessageMetadata.Utenlandsk(land = "DK", journalPostId = "journal-utenlandsk-min"),
        validation = okValidationEmpty(),
        sykmelding =
            Sykmelding.Utenlandsk(
                id = "ea381d44-3ada-46cb-8f6b-dd1825973f53",
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

internal fun xmlSykmeldingFull(id: String) =
    Sykmelding.Xml(
        id = id,
        metadata = sykmeldingMetadataFull(),
        pasient = pasientFull(),
        medisinskVurdering = legacyMedisinskVurderingFull(),
        aktivitet = aktivitetVariety(),
        arbeidsgiver = enArbeidsgiverFull(),
        behandler = behandlerFull(),
        sykmelder = sykmelderFull(),
        prognose = prognoseFull(),
        tiltak = Tiltak(tiltakNav = "Tiltak Nav", andreTiltak = "Andre tiltak"),
        bistandNav = bistandNav(),
        tilbakedatering = tilbakedatering(),
        utdypendeOpplysninger = utdypendeOpplysningerFull(),
    )

internal fun xmlSykmeldingMin(id: String) =
    Sykmelding.Xml(
        id = id,
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

internal fun msgInfoFull(): MessageInfo =
    MessageInfo(
        type = Meldingstype.SYKMELDING,
        genDate = genDate,
        msgId = "msg-id",
        migVersjon = "v1",
    )

internal fun msgInfoMin(): MessageInfo =
    MessageInfo(
        type = Meldingstype.SYKMELDING,
        genDate = genDate,
        msgId = "msg-id",
        migVersjon = null,
    )

internal fun aktivitetVariety() =
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
        Aktivitet.Gradert(
            grad = 50,
            fom = fom.plusDays(8),
            tom = fom.plusDays(14),
            reisetilskudd = false,
        ),
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

internal fun aktivitetIkkeMuligMin() =
    Aktivitet.IkkeMulig(medisinskArsak = null, arbeidsrelatertArsak = null, fom = fom, tom = tom)

internal fun pasientFull() =
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

internal fun pasientMin() = Pasient(null, null, null, "12345678901", emptyList())

internal fun behandlerFull() =
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

internal fun behandlerMin() =
    Behandler(
        navn = Navn("fornavn", null, "etternavn"),
        adresse = null,
        ids = emptyList(),
        kontaktinfo = emptyList(),
    )

internal fun sykmelderFull() =
    Sykmelder(
        ids =
            listOf(PersonId("12345678901", PersonIdType.FNR), PersonId("987654", PersonIdType.HPR)),
        helsepersonellKategori = HelsepersonellKategori.LEGE,
    )

internal fun sykmelderMin() = Sykmelder(emptyList(), HelsepersonellKategori.LEGE)

internal fun enArbeidsgiverFull() =
    ArbeidsgiverInfo.En(
        navn = "ACME AS",
        yrkesbetegnelse = "Utvikler",
        stillingsprosent = 100,
        meldingTilArbeidsgiver = "Vennligst tilrettelegg",
        tiltakArbeidsplassen = "Hev/senk-pult",
    )

internal fun sykmeldingMetadataFull() =
    SykmeldingMeta.Legacy(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("avsender-app", "2.5"),
        behandletTidspunkt = behandletTidspunkt,
        regelsettVersjon = "1234",
        strekkode = "1234567890",
    )

internal fun sykmeldingMetadataMin() =
    SykmeldingMeta.Legacy(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("min", "1"),
        behandletTidspunkt = behandletTidspunkt,
        regelsettVersjon = null,
        strekkode = null,
    )

internal fun legacyMedisinskVurderingFull() =
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

internal fun bidiagnoser(): List<DiagnoseInfo> =
    listOf(
        DiagnoseInfo(DiagnoseSystem.ICD10, "J06.9", "URI"),
        DiagnoseInfo(DiagnoseSystem.ICPC2, "L84", "Rygglidelse"),
    )

internal fun legacyMedisinskVurderingMin() =
    MedisinskVurdering.Legacy(
        hovedDiagnose = null,
        biDiagnoser = emptyList(),
        svangerskap = false,
        yrkesskade = null,
        skjermetForPasient = false,
        syketilfelletStartDato = null,
        annenFraversArsak = null,
    )

internal fun digitalMedisinskVurderingMin() =
    MedisinskVurdering.Digital(
        hovedDiagnose = null,
        biDiagnoser = emptyList(),
        svangerskap = false,
        yrkesskade = null,
        skjermetForPasient = false,
        annenFravarsgrunn = null,
    )

internal fun prognoseFull() =
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

internal fun utdypendeOpplysningerFull() =
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

internal fun mottakenhetBlokkFull() =
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

internal fun mottakenhetBlokkMin() =
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

internal fun organisasjonFull(navn: String) =
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

internal fun organisasjonMin(navn: String) =
    Organisasjon(
        navn = navn,
        type = OrganisasjonsType.IKKE_OPPGITT,
        ids = emptyList(),
        adresse = null,
        kontaktinfo = emptyList(),
        underOrganisasjon = null,
        helsepersonell = null,
    )

internal fun metadataPasientFull() =
    no.nav.tsm.sykmelding.input.core.model.metadata.Pasient(
        ids = listOf(PersonId("12345678901", PersonIdType.FNR)),
        navn = Navn("Ola", null, "Nordmann"),
        fodselsdato = LocalDate.parse("1990-05-15"),
        kjonn = Kjonn.MANN,
        nasjonalitet = "NO",
        adresse = null,
        kontaktinfo = emptyList(),
    )

internal fun fullValidation() =
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

internal fun okValidationEmpty() =
    ValidationResult(status = RuleType.OK, timestamp = validationTime, rules = emptyList())

internal fun bistandNav(): BistandNav =
    BistandNav(bistandUmiddelbart = true, beskrivBistand = "Trenger oppfølging")

internal fun tilbakedatering(): Tilbakedatering =
    Tilbakedatering(
        kontaktDato = LocalDate.parse("2024-01-10"),
        begrunnelse = "Pasient kunne ikke møte tidligere",
    )

internal fun medisinskVurderingDigital(): MedisinskVurdering.Digital =
    MedisinskVurdering.Digital(
        hovedDiagnose = hovedDiagnose(),
        biDiagnoser = bidiagnoser(),
        svangerskap = true,
        yrkesskade = Yrkesskade(yrkesskadeDato = LocalDate.parse("2023-08-01")),
        skjermetForPasient = false,
        annenFravarsgrunn = AnnenFravarsgrunn.NODVENDIG_KONTROLLUNDENRSOKELSE,
    )

internal fun digitalSykmeldingMetadata(): SykmeldingMeta.Digital =
    SykmeldingMeta.Digital(
        mottattDato = mottattDato,
        genDate = genDate,
        avsenderSystem = AvsenderSystem("avsender-app", "2.5"),
    )

internal fun hovedDiagnose(): DiagnoseInfo =
    DiagnoseInfo(DiagnoseSystem.ICPC2B, "R74.0001", "diagnose-tekst")
