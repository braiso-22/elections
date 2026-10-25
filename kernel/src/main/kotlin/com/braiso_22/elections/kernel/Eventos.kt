package com.braiso_22.elections.kernel

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

interface EventoDeDominio

interface BusDeEventos {
    suspend fun publicar(eventos: List<EventoDeDominio>)
    fun <E : EventoDeDominio> suscribir(tipo: KClass<E>, manejador: suspend (E) -> Unit)
}

inline fun <reified E : EventoDeDominio> BusDeEventos.suscribir(noinline manejador: suspend (E) -> Unit) =
    suscribir(E::class, manejador)

/**
 * Entrega los eventos en la misma corrutina que los publica, así que cuando termina el caso de uso
 * los suscriptores (p. ej. el recuento) ya están actualizados. Si un suscriptor falla, el error
 * llega a quien publica: con persistencia real habrá que pasar a un outbox.
 */
class BusDeEventosEnProceso : BusDeEventos {
    private val suscripciones = CopyOnWriteArrayList<Suscripcion<*>>()

    override suspend fun publicar(eventos: List<EventoDeDominio>) {
        eventos.forEach { evento ->
            suscripciones.filter { it.tipo.isInstance(evento) }.forEach { it.entregar(evento) }
        }
    }

    override fun <E : EventoDeDominio> suscribir(tipo: KClass<E>, manejador: suspend (E) -> Unit) {
        suscripciones += Suscripcion(tipo, manejador)
    }

    private class Suscripcion<E : EventoDeDominio>(
        val tipo: KClass<E>,
        private val manejador: suspend (E) -> Unit,
    ) {
        @Suppress("UNCHECKED_CAST")
        suspend fun entregar(evento: EventoDeDominio) = manejador(evento as E)
    }
}

/** Agregado que acumula los eventos que produce hasta que el caso de uso los publica. */
abstract class RaizDeAgregado {
    private val eventos = mutableListOf<EventoDeDominio>()

    protected fun registrar(evento: EventoDeDominio) {
        eventos += evento
    }

    fun extraerEventos(): List<EventoDeDominio> = eventos.toList().also { eventos.clear() }
}
