package com.braiso_22.elections.identidad.infrastructure

import com.braiso_22.elections.identidad.application.AutorizarAdministracion
import com.braiso_22.elections.kernel.VotanteId
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

const val AUTH_VOTANTE = "votante"

data class VotanteAutenticado(val id: VotanteId)

/**
 * Autenticación de desarrollo: el token Bearer ES el id del votante, sin verificar nada.
 * Con Supabase se sustituye por `jwt(AUTH_VOTANTE) { ... }` tomando el votante del claim `sub`;
 * el resto de la aplicación no cambia porque solo ve [VotanteAutenticado].
 */
fun AuthenticationConfig.votantesDeDesarrollo() {
    bearer(AUTH_VOTANTE) {
        authenticate { credencial -> VotanteAutenticado(VotanteId(credencial.token)) }
    }
}

/** Solo válido en rutas montadas dentro de `authenticate(AUTH_VOTANTE)`. */
fun ApplicationCall.votante(): VotanteId =
    checkNotNull(principal<VotanteAutenticado>()) { "Ruta sin autenticar: móntala dentro de authenticate(AUTH_VOTANTE)" }.id

/** Rutas que exigen un votante autenticado que además sea administrador (si no, 401 o 403). */
fun Route.soloAdministradores(build: Route.() -> Unit): Route = authenticate(AUTH_VOTANTE) {
    val autorizar by inject<AutorizarAdministracion>()
    install(SoloAdministradores) { this.autorizar = autorizar }
    build()
}

private class SoloAdministradoresConfig {
    lateinit var autorizar: AutorizarAdministracion
}

private val SoloAdministradores = createRouteScopedPlugin("SoloAdministradores", ::SoloAdministradoresConfig) {
    val autorizar = pluginConfig.autorizar
    on(AuthenticationChecked) { call ->
        // Sin principal la autenticación ya ha respondido 401.
        call.principal<VotanteAutenticado>()?.let { autorizar(it.id) }
    }
}
