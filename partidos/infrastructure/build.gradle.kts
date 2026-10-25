plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(project(":partidos-core"))
    implementation(project(":shared"))
    implementation(libs.ktor.serverCore)
    implementation(libs.koin.ktor)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
