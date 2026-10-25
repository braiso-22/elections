package com.braiso_22.elections.kernel

/**
 * Raíz de los errores de dominio. Cada contexto define los suyos heredando de una de las
 * categorías, y la capa HTTP traduce la categoría a un status code sin conocer los errores concretos.
 */
abstract class ErrorDeDominio(mensaje: String) : RuntimeException(mensaje)

/** Lo que se pide no existe. */
abstract class RecursoNoEncontrado(mensaje: String) : ErrorDeDominio(mensaje)

/** La operación choca con el estado actual del agregado. */
abstract class OperacionNoPermitida(mensaje: String) : ErrorDeDominio(mensaje)

/** Los datos recibidos incumplen una regla del dominio. */
open class DatoNoValido(mensaje: String) : ErrorDeDominio(mensaje)

/** Quien lo pide no tiene permiso para hacerlo. */
abstract class AccesoNoPermitido(mensaje: String) : ErrorDeDominio(mensaje)

/** Otro proceso modificó el agregado entre su lectura y su guardado. */
class ConflictoDeConcurrencia(mensaje: String) : OperacionNoPermitida(mensaje)

fun validar(condicion: Boolean, mensaje: () -> String) {
    if (!condicion) throw DatoNoValido(mensaje())
}
