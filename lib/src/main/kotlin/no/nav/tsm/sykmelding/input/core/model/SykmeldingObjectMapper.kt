package no.nav.tsm.sykmelding.input.core.model

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

private val kotlinModule = KotlinModule.Builder().enable(KotlinFeature.StrictNullChecks).build()

val sykmeldingObjectMapper =
    JsonMapper.builder()
        .addModules(kotlinModule, SykmeldingModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .build()
