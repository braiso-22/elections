package com.braiso_22.elections.resultados.infrastructure

import com.braiso_22.elections.resultados.application.ConsultarResultados
import com.braiso_22.elections.resultados.application.ContabilizarVotos
import com.braiso_22.elections.resultados.domain.ConsultaCandidaturas
import com.braiso_22.elections.resultados.domain.RepositorioRecuentos
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val resultadosModule = module {
    singleOf(::InMemoryRecuentoRepository) bind RepositorioRecuentos::class
    singleOf(::EleccionesCandidaturasAdapter) bind ConsultaCandidaturas::class
    singleOf(::ContabilizarVotos)
    singleOf(::ConsultarResultados)
    singleOf(::SuscriptorDeVotacion)
}
