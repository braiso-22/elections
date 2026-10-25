package com.braiso_22.elections.identidad.infrastructure

import com.braiso_22.elections.identidad.application.AutorizarAdministracion
import com.braiso_22.elections.identidad.domain.RegistroDeAdministradores
import com.braiso_22.elections.kernel.VotanteId
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun identidadModule(administradores: Set<VotanteId>) = module {
    single<RegistroDeAdministradores> { InMemoryAdministradores(administradores) }
    singleOf(::AutorizarAdministracion)
}
