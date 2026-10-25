plugins {
    alias(libs.plugins.kotlinJvm)
}

// Un core solo puede depender del kernel: ni Ktor, ni Koin, ni otro contexto.
dependencies {
    api(project(":kernel"))
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
