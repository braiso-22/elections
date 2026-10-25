package com.braiso_22.elections.partidos.domain

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.kernel.RecursoNoEncontrado

class PartidoNoEncontrado(id: PartidoId) : RecursoNoEncontrado("No existe el partido ${id.valor}")
