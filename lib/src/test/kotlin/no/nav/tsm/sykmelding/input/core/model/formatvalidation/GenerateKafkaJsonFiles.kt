package no.nav.tsm.sykmelding.input.core.model.formatvalidation

import java.nio.file.Files
import java.nio.file.Path
import no.nav.tsm.sykmelding.input.core.model.kafkaJsonSamples
import no.nav.tsm.sykmelding.input.core.model.sykmeldingObjectMapper
import tools.jackson.databind.SerializationFeature

fun main() {
    val version =
        System.getProperty("version")
            ?: error(
                "version system property not set. Run via: ./gradlew :lib:generateKafkaJsonFiles"
            )
    val outputDir = Path.of("lib/src/test/resources/format/v$version").toAbsolutePath()
    Files.createDirectories(outputDir)
    println("Writing json file (version=$version) to: $outputDir")

    val mapper = sykmeldingObjectMapper.rebuild().enable(SerializationFeature.INDENT_OUTPUT).build()

    kafkaJsonSamples().forEach { (name, record) ->
        val path = outputDir.resolve(name)
        val json = mapper.writeValueAsString(record)
        Files.writeString(path, json + "\n")
    }
}
