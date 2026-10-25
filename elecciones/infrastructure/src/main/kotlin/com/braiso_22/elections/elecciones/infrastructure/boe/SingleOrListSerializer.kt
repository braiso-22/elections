package com.braiso_22.elections.elecciones.infrastructure.boe

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * The BOE API returns a single object instead of a one-element array when a list has only one entry.
 */
open class SingleOrListSerializer<T>(elementSerializer: KSerializer<T>) :
    JsonTransformingSerializer<List<T>>(ListSerializer(elementSerializer)) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonArray) element else JsonArray(listOf(element))
}

object DiarioListSerializer : SingleOrListSerializer<Diario>(Diario.serializer())
object SeccionListSerializer : SingleOrListSerializer<Seccion>(Seccion.serializer())
object DepartamentoListSerializer : SingleOrListSerializer<Departamento>(Departamento.serializer())
object EpigrafeListSerializer : SingleOrListSerializer<Epigrafe>(Epigrafe.serializer())
object ItemListSerializer : SingleOrListSerializer<Item>(Item.serializer())
