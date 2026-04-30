package no.nav.tsm.sykmelding.input.core.model.formatvalidation

import com.fasterxml.jackson.databind.JsonNode
import no.nav.tsm.sykmelding.input.core.model.SykmeldingRecord
import no.nav.tsm.sykmelding.input.core.model.kafkaJsonSamples
import no.nav.tsm.sykmelding.input.core.model.sykmeldingObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class KafkaJsonStructureTest {

    @TestFactory
    fun `current serialization preserves every field from every committed version`():
        List<DynamicTest> {
        val samples = kafkaJsonSamples()
        return formatVersionDirs().flatMap { versionDir ->
            versionDir.jsonFiles().map { jsonFile ->
                dynamicTest("${versionDir.name}/${jsonFile.name}") {
                    val sample =
                        samples[jsonFile.name]
                            ?: error(
                                "No matching sample in kafkaJsonSamples() for json file: ${jsonFile.name}"
                            )
                    val expected = sykmeldingObjectMapper.readTree(jsonFile)
                    val actual = serialize(sample)
                    val actualWithNewFieldsRemoved = removeFieldsNotInTemplate(actual, expected)
                    assertEquals(expected, actualWithNewFieldsRemoved)
                }
            }
        }
    }

    private fun serialize(record: SykmeldingRecord): JsonNode {
        val str = sykmeldingObjectMapper.writeValueAsString(record)
        return sykmeldingObjectMapper.readTree(str)
    }
}

private fun removeFieldsNotInTemplate(source: JsonNode, template: JsonNode): JsonNode =
    when {
        source.isObject && template.isObject -> {
            val out = sykmeldingObjectMapper.createObjectNode()
            template.fields().forEach { (key, valueInTemplate) ->
                source.get(key)?.let { valueInSource ->
                    out.set<JsonNode>(
                        key,
                        removeFieldsNotInTemplate(valueInSource, valueInTemplate),
                    )
                }
            }
            out
        }
        source.isArray && template.isArray && source.size() == template.size() -> {
            val out = sykmeldingObjectMapper.createArrayNode()
            source.zip(template).forEach { (s, t) -> out.add(removeFieldsNotInTemplate(s, t)) }
            out
        }
        else -> source
    }
