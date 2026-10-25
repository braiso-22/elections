package com.braiso_22.elections

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Gradle ya impide que un core dependa de lo que no declara; esto vigila además que nadie
 * le añada dependencias de framework o de otro contexto.
 */
class ArquitecturaTest {

    private val raiz = File(checkNotNull(System.getProperty("elections.rootDir")) { "Falta -Delections.rootDir" })
    private val contextos = listOf("partidos", "elecciones", "votacion", "resultados", "identidad")
    private val base = "com.braiso_22.elections"

    @Test
    fun losCoreSoloDependenDeKotlinDelKernelYDeSuPropioContexto() {
        val permitidos = listOf("kotlin.", "java.", "$base.kernel.")
        val infracciones = contextos.flatMap { contexto ->
            importsDe(File(raiz, "$contexto/core/src/main/kotlin"))
                .filterNot { (_, import) -> (permitidos + "$base.$contexto.").any(import::startsWith) }
        }
        assertTrue(infracciones.isEmpty(), "Imports prohibidos en los core:\n" + infracciones.formatear())
    }

    @Test
    fun elKernelNoDependeDeNadie() {
        val infracciones = importsDe(File(raiz, "kernel/src/main/kotlin"))
            .filterNot { (_, import) -> listOf("kotlin.", "java.", "$base.kernel.").any(import::startsWith) }
        assertTrue(infracciones.isEmpty(), "Imports prohibidos en el kernel:\n" + infracciones.formatear())
    }

    @Test
    fun laInfraestructuraSoloUsaLaDeIdentidadDeOtroContexto() {
        val infracciones = contextos.flatMap { contexto ->
            importsDe(File(raiz, "$contexto/infrastructure/src/main/kotlin")).filter { (_, import) ->
                val deOtraInfraestructura = contextos
                    .filter { it != contexto && it != "identidad" }
                    .any { import.startsWith("$base.$it.infrastructure.") }
                deOtraInfraestructura
            }
        }
        assertTrue(infracciones.isEmpty(), "Infraestructura acoplada a la de otro contexto:\n" + infracciones.formatear())
    }

    private fun importsDe(directorio: File): List<Pair<File, String>> {
        assertTrue(directorio.isDirectory, "No existe $directorio")
        return directorio.walkTopDown()
            .filter { it.extension == "kt" }
            .flatMap { fichero ->
                fichero.readLines()
                    .filter { it.startsWith("import ") }
                    .map { fichero to it.removePrefix("import ").trim() }
            }
            .toList()
    }

    private fun List<Pair<File, String>>.formatear() =
        joinToString("\n") { (fichero, import) -> "  ${fichero.relativeTo(raiz)}: $import" }
}
