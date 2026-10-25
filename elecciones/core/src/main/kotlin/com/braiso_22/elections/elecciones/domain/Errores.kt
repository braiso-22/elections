package com.braiso_22.elections.elecciones.domain

import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.OperacionNoPermitida
import com.braiso_22.elections.kernel.RecursoNoEncontrado

class EleccionNoEncontrada(id: EleccionId) : RecursoNoEncontrado("No existe la elección ${id.valor}")

class ConvocatoriaNoPublicada(referencia: ReferenciaBoe) :
    RecursoNoEncontrado("El BOE no tiene ninguna convocatoria con referencia ${referencia.valor}")

class ConvocatoriaCongelada(id: EleccionId) :
    OperacionNoPermitida("La votación de la elección ${id.valor} ya ha empezado y su convocatoria no se puede modificar")

class PartidosNoRegistrados(siglas: List<String>) :
    DatoNoValido("Hay candidaturas de partidos que no están en el catálogo: ${siglas.joinToString()}")
