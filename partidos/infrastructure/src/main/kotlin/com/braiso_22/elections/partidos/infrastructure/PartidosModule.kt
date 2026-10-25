package com.braiso_22.elections.partidos.infrastructure

import com.braiso_22.elections.partidos.application.BuscarPartidoPorSiglas
import com.braiso_22.elections.partidos.application.ImportarPartidosDelRegistro
import com.braiso_22.elections.partidos.application.ListarPartidos
import com.braiso_22.elections.partidos.application.ObtenerPartido
import com.braiso_22.elections.partidos.domain.RegistroDePartidos
import com.braiso_22.elections.partidos.domain.RepositorioPartidos
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val partidosModule = module {
    singleOf(::InMemoryPartidoRepository) bind RepositorioPartidos::class
    singleOf(::SampleRegistroDePartidos) bind RegistroDePartidos::class
    singleOf(::ListarPartidos)
    singleOf(::ObtenerPartido)
    singleOf(::BuscarPartidoPorSiglas)
    singleOf(::ImportarPartidosDelRegistro)
}
