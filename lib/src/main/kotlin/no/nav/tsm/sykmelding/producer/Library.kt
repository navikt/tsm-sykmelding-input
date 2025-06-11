package no.nav.tsm.sykmelding.producer

import no.nav.tsm.sykmelding.producer.metrics.SykmeldingProducerVersion

class Library {
    fun someLibraryMethod(): Boolean {
        println(SykmeldingProducerVersion.VERSION)

        if (SykmeldingProducerVersion.VERSION == "3") {
            println("Gratulerer med dagen!")
        }

        return true
    }
}
