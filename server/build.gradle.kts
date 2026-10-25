plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)
}

group = "com.braiso_22.elections"
version = "1.0.0"
application {
    mainClass = "com.braiso_22.elections.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

// Composition root del monolito modular: arranca Ktor y monta los contextos.
dependencies {
    implementation(project(":kernel"))
    implementation(project(":shared"))
    implementation(project(":partidos-infrastructure"))
    implementation(project(":elecciones-infrastructure"))
    implementation(project(":votacion-infrastructure"))
    implementation(project(":resultados-infrastructure"))
    implementation(project(":identidad-infrastructure"))
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverStatusPages)
    implementation(libs.ktor.serverCallLogging)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serializationKotlinxJson)
    implementation(libs.koin.ktor)
    implementation(libs.koin.loggerSlf4j)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.ktor.clientContentNegotiation)
    testImplementation(libs.kotlin.testJunit)
}

tasks.test {
    // El test de arquitectura lee el código de los módulos core.
    systemProperty("elections.rootDir", rootDir.absolutePath)
}
