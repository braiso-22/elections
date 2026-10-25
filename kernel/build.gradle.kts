plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)
}
