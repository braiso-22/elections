package com.braiso_22.elections.kernel

import kotlin.uuid.Uuid

// Identificadores que cruzan contextos. Cada contexto referencia los agregados de otro solo por su id.

@JvmInline
value class PartidoId(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "El id de partido no puede estar vacío" }
    }

    companion object {
        fun nuevo() = PartidoId(Uuid.random().toString())
    }
}

@JvmInline
value class EleccionId(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "El id de elección no puede estar vacío" }
    }

    companion object {
        fun nuevo() = EleccionId(Uuid.random().toString())
    }
}

@JvmInline
value class CandidaturaId(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "El id de candidatura no puede estar vacío" }
    }

    companion object {
        fun nuevo() = CandidaturaId(Uuid.random().toString())
    }
}

/** Sujeto que da el proveedor de identidad (OIDC). Lo genera él, nunca la aplicación. */
@JvmInline
value class VotanteId(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "El id de votante no puede estar vacío" }
    }
}
