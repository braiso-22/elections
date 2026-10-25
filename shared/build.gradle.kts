plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

// Contrato HTTP entre el servidor Ktor y la web en React: si cambia un DTO, cambia para los dos.
kotlin {
    jvm()

    js {
        outputModuleName = "shared"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
    }

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serializationCore)
        }
    }
}
