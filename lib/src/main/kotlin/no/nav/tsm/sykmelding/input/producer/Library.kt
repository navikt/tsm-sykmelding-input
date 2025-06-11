package no.nav.tsm.sykmelding.input.producer

import no.nav.tsm.sykmelding.input.metrics.SykmeldingProducerVersion

class Library {
    fun someLibraryMethod(): Boolean {
        println(SykmeldingProducerVersion.VERSION)

        if (SykmeldingProducerVersion.VERSION == "3") {
            println("Gratulerer med dagen!")
        }

        return true
    }
}
