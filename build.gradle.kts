import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.gitlab.arturbosch.detekt").version("1.23.7")
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "21"
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}


repositories {
    mavenCentral()
}

tasks {
    val compileSolidity by creating(Exec::class) {
        commandLine = listOf(
            "/opt/homebrew/bin/solc",
            "--bin",
            "--abi",
            "src/main/solidity/xmlContract.sol",
            "-o",
            "src/main/resources/",
            "--overwrite"
        )
    }

    compileKotlin {
        dependsOn(compileSolidity)
    }

    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            allWarningsAsErrors = false
            jvmTarget.set(JVM_21)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
    }
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.32.4.0"))
    implementation(platform("org.http4k:http4k-connect-bom:5.24.1.0"))
    implementation("org.http4k:http4k-aws")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-connect-amazon-cloudwatchlogs")
    implementation("org.http4k:http4k-connect-storage-jdbc")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-format-jackson-xml")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-metrics-micrometer")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-template-handlebars")

    implementation("ch.qos.logback:logback-classic:1.5.16")

    implementation("com.google.protobuf:protobuf-java:4.29.3")
    implementation("com.zaxxer:HikariCP:6.0.0")

    implementation("io.grpc:grpc-netty-shaded:1.70.0")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.hyperledger.fabric:fabric-gateway:1.7.1")
    implementation("org.ktorm:ktorm-core:3.3.0")
    implementation("org.web3j:core:4.12.3")

    implementation("software.amazon.awssdk:secretsmanager:2.29.1")

    testImplementation("org.http4k:http4k-connect-amazon-cloudwatchlogs-fake")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.http4k:http4k-testing-hamkrest")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.1")
}
