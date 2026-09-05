package com.voxelteamgames.morphjump.game

object ObjectRegistry {

    private val objects =
        mutableMapOf<String, ObjectDefinition>()

    fun register(
        definition: ObjectDefinition
    ) {

        require(
            !objects.containsKey(definition.id)
        ) {
            "Objeto já registrado: ${definition.id}"
        }

        objects[definition.id] = definition
    }

    fun get(
        id: String
    ): ObjectDefinition {

        return objects[id]
            ?: error(
                "Objeto não registrado: $id"
            )
    }

    fun clear() {

        objects.clear()
    }
}