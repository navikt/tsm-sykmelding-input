package no.nav.tsm.sykmelding.input.core.model.formatvalidation

import java.io.File
import no.nav.tsm.sykmelding.input.core.model.SykmeldingRecord
import no.nav.tsm.sykmelding.input.core.model.sykmeldingObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class SykmeldingRecordDeserializerTest {

    @TestFactory
    fun `every jsonfile under format parses`(): List<DynamicTest> {
        val root = File("src/test/resources/format")
        if (!root.isDirectory) return emptyList()
        return root
            .walkTopDown()
            .onEnter { it.name != "invalid" }
            .filter { it.isFile && it.name.endsWith(".json") }
            .sortedBy { it.relativeTo(root).path }
            .map { jsonFile ->
                dynamicTest(jsonFile.relativeTo(root).path) {
                    val record =
                        sykmeldingObjectMapper.readValue(jsonFile, SykmeldingRecord::class.java)
                    assertNotNull(record)
                }
            }
            .toList()
    }

    @TestFactory
    fun `every jsonfile under manual-invalid fails to parse`(): List<DynamicTest> {
        val root = File("src/test/resources/format/manual/invalid")
        if (!root.isDirectory) return emptyList()
        return root
            .walkTopDown()
            .filter { it.isFile && it.name.endsWith(".json") }
            .sortedBy { it.relativeTo(root).path }
            .map { jsonFile ->
                dynamicTest(jsonFile.relativeTo(root).path) {
                    assertThrows(IllegalArgumentException::class.java) {
                        sykmeldingObjectMapper.readValue(jsonFile, SykmeldingRecord::class.java)
                    }
                }
            }
            .toList()
    }
}

internal fun formatVersionDirs(): List<File> =
    File("src/test/resources/format")
        .listFiles { f -> f.isDirectory && f.name.startsWith("v") }
        .orEmpty()
        .sortedBy { it.name }

internal fun File.jsonFiles(): List<File> =
    listFiles { f -> f.name.endsWith(".json") }.orEmpty().sortedBy { it.name }
