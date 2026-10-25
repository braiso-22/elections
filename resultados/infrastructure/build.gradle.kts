plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(project(":resultados-core"))
    implementation(project(":votacion-core"))
    implementation(project(":elecciones-core"))
    implementation(project(":shared"))
    implementation(libs.ktor.serverCore)
    implementation(libs.koin.ktor)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
