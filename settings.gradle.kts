rootProject.name = "Elections"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":kernel")
include(":shared")
include(":server")

// Un bounded context por carpeta, con dos módulos: core (dominio + aplicación, Kotlin puro)
// e infrastructure (adaptadores). Se renombran a :<contexto>-<capa> para que no haya
// varios proyectos llamados "core" o "infrastructure".
listOf("partidos", "elecciones", "votacion", "resultados", "identidad").forEach { contexto ->
    listOf("core", "infrastructure").forEach { capa ->
        include(":$contexto-$capa")
        project(":$contexto-$capa").projectDir = file("$contexto/$capa")
    }
}
