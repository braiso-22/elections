package com.braiso_22.elections.votacion.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.OperacionNoPermitida
import com.braiso_22.elections.kernel.RecursoNoEncontrado

class EleccionDesconocida(id: EleccionId) : RecursoNoEncontrado("No existe la elección ${id.valor}")

class EleccionNoAbierta(id: EleccionId) : OperacionNoPermitida("La elección ${id.valor} no está abierta a votación")

class CandidaturaNoPresentada(eleccionId: EleccionId, candidaturaId: CandidaturaId) :
    DatoNoValido("La candidatura ${candidaturaId.valor} no se presenta a la elección ${eleccionId.valor}")

class VotoNoEncontrado(eleccionId: EleccionId) : RecursoNoEncontrado("Todavía no has votado en la elección ${eleccionId.valor}")
