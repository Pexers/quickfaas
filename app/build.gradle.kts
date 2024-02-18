import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

group = "com.pexers.quickfaas"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    val ktorVersion = "2.0.3"
    implementation(compose.desktop.currentOs)
    implementation("ch.qos.logback:logback-classic:1.2.11")                     // Required by some libraries
    implementation("org.apache.maven.shared:maven-invoker:3.2.0")               // Maven invoker
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")                 // Sessions for tokens
    implementation("io.ktor:ktor-server-auth:$ktorVersion")                     // OAuth
    implementation("io.ktor:ktor-client-cio:$ktorVersion")                      // CIO engine for HTTP client
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    /** Metrics tests **/
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")   // For 'runTest' coroutine
    testImplementation("com.google.cloud:google-cloud-monitoring:3.2.9")
    testImplementation("com.google.auth:google-auth-library-oauth2-http:1.6.0")
    testImplementation("com.google.protobuf:protobuf-java-util:3.20.1")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "controller.MainKt"
        javaHome = "C:\\Java\\jdk16.0.2"
        nativeDistributions {
            // TODO: find a way to include 'function-deployment' folder when creating distributable
            modules("java.naming")
            packageName = "QuickFaaS"
            packageVersion = "1.0.0"
            copyright = "Copyright © 7/4/2022, Pexers (https://github.com/Pexers). All rights reserved."
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            windows {
                // a version for all Windows distributables
                packageVersion = "1.0.0"
                // a version only for the msi package
                msiPackageVersion = "1.0.0"
                // a version only for the exe package
                exePackageVersion = "1.0.0"
            }

        }
    }
}
