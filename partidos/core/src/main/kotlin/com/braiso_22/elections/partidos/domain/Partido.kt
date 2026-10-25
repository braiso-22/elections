package com.braiso_22.elections.partidos.domain

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.kernel.validar

/** Partido inscrito en el Registro de Partidos Políticos. Existe aunque no se presente a ninguna elección. */
class Partido(
    val id: PartidoId,
    val numeroDeRegistro: NumeroDeRegistro,
    val nombre: String,
    val siglas: Siglas,
) {
    init {
        validar(nombre.isNotBlank()) { "El nombre del partido no puede estar vacío" }
    }

    fun coincideCon(texto: String): Boolean =
        nombre.contains(texto, ignoreCase = true) || siglas.valor.contains(texto, ignoreCase = true)

    /** El registro manda: si cambia el nombre o las siglas inscritas, el partido se actualiza. */
    fun actualizadoCon(inscripcion: PartidoInscrito): Partido =
        Partido(id, numeroDeRegistro, inscripcion.nombre, inscripcion.siglas)

    fun difiereDe(inscripcion: PartidoInscrito): Boolean =
        nombre != inscripcion.nombre || siglas != inscripcion.siglas

    companion object {
        fun inscribir(inscripcion: PartidoInscrito) = Partido(
            id = PartidoId.nuevo(),
            numeroDeRegistro = inscripcion.numeroDeRegistro,
            nombre = inscripcion.nombre,
            siglas = inscripcion.siglas,
        )
    }
}

@JvmInline
value class NumeroDeRegistro(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "El número de registro no puede estar vacío" }
    }
}

@JvmInline
value class Siglas(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "Las siglas no pueden estar vacías" }
    }

    fun equivalenA(otras: String): Boolean = valor.equals(otras.trim(), ignoreCase = true)
}
