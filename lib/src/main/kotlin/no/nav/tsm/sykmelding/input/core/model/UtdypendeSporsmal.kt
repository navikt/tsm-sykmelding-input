package no.nav.tsm.sykmelding.input.core.model

enum class Sporsmalstype {
    UTFORDRINGER_MED_GRADERT_ARBEID,
    UTFORDRINGER_MED_ARBEID,
    MEDISINSK_OPPSUMMERING,
    HENSYN_PA_ARBEIDSPLASSEN,
    BEHANDLING_OG_FREMTIDIG_ARBEID,
    UAVKLARTE_FORHOLD,
    FORVENTET_HELSETILSTAND_UTVIKLING,
    MEDISINSKE_HENSYN,
}

data class UtdypendeSporsmal(
    val svar: String,
    val type: Sporsmalstype,
    val skjermetForArbeidsgiver: Boolean = true,
    val sporsmal: String?,
)
