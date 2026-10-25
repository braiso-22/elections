package com.braiso_22.elections.votacion.infrastructure

import com.braiso_22.elections.votacion.application.ConsultarMiVoto
import com.braiso_22.elections.votacion.application.Votar
import com.braiso_22.elections.votacion.domain.ConsultaElecciones
import com.braiso_22.elections.votacion.domain.RepositorioVotos
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val votacionModule = module {
    singleOf(::InMemoryVotoRepository) bind RepositorioVotos::class
    singleOf(::EleccionesConsultaAdapter) bind ConsultaElecciones::class
    singleOf(::Votar)
    singleOf(::ConsultarMiVoto)
}
