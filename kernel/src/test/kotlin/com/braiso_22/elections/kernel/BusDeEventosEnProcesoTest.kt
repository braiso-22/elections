package com.braiso_22.elections.kernel

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BusDeEventosEnProcesoTest {

    private data class Saludo(val texto: String) : EventoDeDominio
    private data class Despedida(val texto: String) : EventoDeDominio

    @Test
    fun entregaCadaEventoSoloALosSuscriptoresDeSuTipo() = runTest {
        val bus = BusDeEventosEnProceso()
        val saludos = mutableListOf<Saludo>()
        val todos = mutableListOf<EventoDeDominio>()
        bus.suscribir<Saludo> { saludos += it }
        bus.suscribir<EventoDeDominio> { todos += it }

        bus.publicar(listOf(Saludo("hola"), Despedida("adiós")))

        assertEquals(listOf(Saludo("hola")), saludos)
        assertEquals(listOf(Saludo("hola"), Despedida("adiós")), todos)
    }
}
