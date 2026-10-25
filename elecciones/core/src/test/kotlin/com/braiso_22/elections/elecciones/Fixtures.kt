package com.braiso_22.elections.elecciones

import com.braiso_22.elections.elecciones.domain.PeriodoVotacion
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

val INICIO: Instant = Instant.parse("2027-05-23T07:00:00Z")
val FIN: Instant = INICIO + 1.days
val PERIODO = PeriodoVotacion(INICIO, FIN)
val ANTES: Instant = INICIO - 10.days

class RelojFijo(var ahora: Instant) : Clock {
    override fun now() = ahora
}
