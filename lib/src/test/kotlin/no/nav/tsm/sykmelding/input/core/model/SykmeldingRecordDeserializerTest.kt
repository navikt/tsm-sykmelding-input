package no.nav.tsm.sykmelding.input.core.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File
import kotlin.collections.forEach
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SykmeldingRecordDeserializerTest {

    @Test
    fun `Deserialize Test from previous versions`() {
        val versionMap = mutableMapOf<File, Array<File>>()
        File("src/test/resources/format").listFiles {it.isDirectory && it.name.startsWith("v")} .map { file ->
            val subfiles = file.listFiles{ it.name.endsWith(".json") }
            versionMap[file] = subfiles
        }
        val tests = versionMap.map { (file, subfiles) ->
            println("testing files from version: ${file.name}")
           file to subfiles.map { subfile ->
                val json = subfile.readText()
                subfile.name to try {
                    val sykmelding = sykmeldingObjectMapper.readValue(json, SykmeldingRecord::class.java)
                    sykmelding != null
                } catch (ex : Exception) {
                    // println("Error during parsing JSON: $json")
                    false
                }
            }.also {
                it.forEach {
                    println("\t  ${if (it.second) "✅" else "❌" } file: ${it.first}")
                }
           }
        }

        tests.forEach {
            it.second.forEach {
                assertTrue { it.second }
            }
        }
    }
}