package no.nav.tsm.sykmelding.input.core.model.formatvalidation

import com.fasterxml.jackson.databind.SerializationFeature
import java.nio.file.Files
import java.nio.file.Path
import no.nav.tsm.sykmelding.input.core.model.kafkaJsonSamples
import no.nav.tsm.sykmelding.input.core.model.sykmeldingObjectMapper

fun main() {
    val version =
        System.getProperty("version")
            ?: error(
                "version system property not set. Run via: ./gradlew :lib:generateKafkaJsonFiles"
            )
    val outputDir = Path.of("lib/src/test/resources/format/v$version").toAbsolutePath()
    Files.createDirectories(outputDir)
    println("Writing json file (version=$version) to: $outputDir")

    val mapper = sykmeldingObjectMapper.copy().enable(SerializationFeature.INDENT_OUTPUT)

    kafkaJsonSamples().forEach { (name, record) ->
        val path = outputDir.resolve(name)
        val json = mapper.writeValueAsString(record)
        Files.writeString(path, json + "\n")
    }
}
