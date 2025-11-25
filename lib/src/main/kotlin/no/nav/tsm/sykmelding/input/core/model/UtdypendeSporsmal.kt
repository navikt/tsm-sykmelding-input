package no.nav.tsm.sykmelding.input.core.model

enum class Sporsmalstype {
    UTFORDRINGER_MED_GRADERT_ARBEID,
    MEDISINSK_OPPSUMERING,
    HENSYN_PA_ARBEIDSPLASSEN,
}

data class UtdypendeSporsmal(
    val svar: String,
    val type: Sporsmalstype,
    val skjermetForArbeidsgiver: Boolean = true,
)
