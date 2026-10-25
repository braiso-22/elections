package com.braiso_22.elections.resultados.application

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.resultados.domain.ConsultaCandidaturas
import com.braiso_22.elections.resultados.domain.EleccionSinResultados
import com.braiso_22.elections.resultados.domain.OpcionContada
import com.braiso_22.elections.resultados.domain.Recuento
import com.braiso_22.elections.resultados.domain.RepositorioRecuentos

/** Mantiene el recuento al día a partir de lo que pasa en Votación. */
class ContabilizarVotos(private val recuentos: RepositorioRecuentos) {
    suspend fun votoEmitido(eleccionId: EleccionId, opcion: OpcionContada) =
        recuentos.actualizar(eleccionId) { it.sumar(opcion) }

    suspend fun votoCambiado(eleccionId: EleccionId, anterior: OpcionContada, nueva: OpcionContada) =
        recuentos.actualizar(eleccionId) { it.cambiar(anterior, nueva) }
}

data class Resultados(
    val eleccionId: EleccionId,
    val participacion: Int,
    val enBlanco: Cifra,
    val contraElSistema: Cifra,
    /** De más a menos votos. */
    val candidaturas: List<ResultadoCandidatura>,
)

data class ResultadoCandidatura(val candidaturaId: CandidaturaId, val denominacion: String, val cifra: Cifra)

data class Cifra(val votos: Int, val porcentaje: Double)

/** Resultados en tiempo real, incluidas las candidaturas que aún no tienen votos. */
class ConsultarResultados(
    private val recuentos: RepositorioRecuentos,
    private val candidaturas: ConsultaCandidaturas,
) {
    suspend operator fun invoke(eleccionId: EleccionId): Resultados {
        val conocidas = candidaturas.candidaturasDe(eleccionId) ?: throw EleccionSinResultados(eleccionId)
        val recuento = recuentos.de(eleccionId) ?: Recuento(eleccionId)
        return Resultados(
            eleccionId = eleccionId,
            participacion = recuento.total,
            enBlanco = recuento.cifraDe(OpcionContada.EnBlanco),
            contraElSistema = recuento.cifraDe(OpcionContada.ContraElSistema),
            candidaturas = conocidas
                .map { ResultadoCandidatura(it.id, it.denominacion, recuento.cifraDe(OpcionContada.Candidatura(it.id))) }
                .sortedByDescending { it.cifra.votos },
        )
    }

    private fun Recuento.cifraDe(opcion: OpcionContada) = Cifra(votosDe(opcion), porcentajeDe(opcion))
}
