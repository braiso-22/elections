package com.braiso_22.elections.partidos.application

import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.partidos.domain.RegistroDePartidos
import com.braiso_22.elections.partidos.domain.RepositorioPartidos

/** Sincroniza el catálogo con el registro. Es idempotente: se identifica cada partido por su número de registro. */
class ImportarPartidosDelRegistro(
    private val registro: RegistroDePartidos,
    private val partidos: RepositorioPartidos,
) {
    data class Resultado(val nuevos: Int, val actualizados: Int)

    suspend operator fun invoke(): Resultado {
        var nuevos = 0
        var actualizados = 0
        registro.partidosInscritos().forEach { inscripcion ->
            val existente = partidos.porNumeroDeRegistro(inscripcion.numeroDeRegistro)
            when {
                existente == null -> {
                    partidos.guardar(Partido.inscribir(inscripcion))
                    nuevos++
                }
                existente.difiereDe(inscripcion) -> {
                    partidos.guardar(existente.actualizadoCon(inscripcion))
                    actualizados++
                }
            }
        }
        return Resultado(nuevos, actualizados)
    }
}
