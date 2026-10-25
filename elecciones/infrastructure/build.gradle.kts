plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    api(project(":elecciones-core"))
    implementation(project(":partidos-core"))
    implementation(project(":shared"))
    implementation(libs.ktor.serverCore)
    implementation(libs.koin.ktor)
    // Cliente de la API de datos abiertos del BOE (aún no conectado al puerto BoletinOficial).
    implementation(libs.ktor.clientCore)
    implementation(libs.ktor.clientCio)
    implementation(libs.ktor.clientContentNegotiation)
    implementation(libs.ktor.serializationKotlinxJson)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
