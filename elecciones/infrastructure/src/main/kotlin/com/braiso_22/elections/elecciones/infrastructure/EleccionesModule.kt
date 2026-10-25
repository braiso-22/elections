package com.braiso_22.elections.elecciones.infrastructure

import com.braiso_22.elections.elecciones.application.ImportarEleccionDelBoe
import com.braiso_22.elections.elecciones.application.ListarElecciones
import com.braiso_22.elections.elecciones.application.ObtenerEleccion
import com.braiso_22.elections.elecciones.domain.BoletinOficial
import com.braiso_22.elections.elecciones.domain.CatalogoDePartidos
import com.braiso_22.elections.elecciones.domain.RepositorioElecciones
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val eleccionesModule = module {
    singleOf(::InMemoryEleccionRepository) bind RepositorioElecciones::class
    singleOf(::SampleBoletinOficial) bind BoletinOficial::class
    singleOf(::PartidosCatalogAdapter) bind CatalogoDePartidos::class
    singleOf(::ListarElecciones)
    singleOf(::ObtenerEleccion)
    singleOf(::ImportarEleccionDelBoe)
}
