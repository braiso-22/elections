plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    api(project(":identidad-core"))
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverCore)
    implementation(libs.koin.ktor)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
