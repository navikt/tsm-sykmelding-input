plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    id("com.diffplug.spotless") version "7.0.2"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api(libs.slf4j)
    api(libs.prometheus)
    api(libs.jackson.module.kotlin)
    api(libs.jackson.datatype.jsr310)
    api(libs.kafka.clients)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
    withSourcesJar()
}

tasks.withType<Jar>().configureEach {
    manifest { attributes["Implementation-Version"] = file("version").readText().trim() }
}

val generateVersionFile =
    tasks.register("generateVersionFile") {
        val version = file("version").readText().trim()
        val outputDir = layout.buildDirectory.dir("generated/sykmelding/input")
        outputs.dir(outputDir)

        doLast {
            val file = outputDir.get().file("SykmeldingProducerVersion.kt").asFile
            file.parentFile.mkdirs()
            file.writeText(
                """
            package no.nav.tsm.sykmelding.input.metrics

            internal object SykmeldingProducerVersion {
                const val VERSION = "$version"
            }
            
            """
                    .trimIndent()
            )
        }
    }

sourceSets["main"].kotlin.srcDir(generateVersionFile.map { it.outputs.files })

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            groupId = "no.nav.tsm.sykmelding"
            artifactId = "input"
            version = file("version").readText().trim()
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/navikt/tsm-sykmelding-input")
            credentials {
                username = "x-access-token"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

spotless { kotlin { ktfmt("0.54").kotlinlangStyle() } }

tasks.named<Test>("test") { useJUnitPlatform() }

tasks.named("sourcesJar") { dependsOn("generateVersionFile") }
