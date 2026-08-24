package com.voxelteamgames.opendash.game

object DefaultObjects {

    fun register() {

        ObjectRegistry.clear()

        ObjectRegistry.register(
            ObjectDefinition(
                id = "block.iron",
                type = ObjectType.BLOCK,
                texture =
                    "/textures/blocks/default/raster_iron_block.png",
                collision = true
            )
        )

        ObjectRegistry.register(
            ObjectDefinition(
                id = "hazard.yellow_spike",
                type = ObjectType.HAZARD,
                texture =
                    "/textures/hazards/default/raster_yellow_spike.png",
                collision = true
            )
        )

        ObjectRegistry.register(
            ObjectDefinition(
                id = "hazard.solid_spike",
                type = ObjectType.HAZARD,
                vector =
                    "/textures/hazards/modern/solid_spike.svg",
                collision = true
            )
        )

        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.modern.circle",
                type = ObjectType.DECO,
                vector =
                    "/textures/deco/modern/circle.svg",
                collision = false
            )
        )

        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.modern.square",
                type = ObjectType.DECO,
                vector =
                    "/textures/deco/modern/square.svg",
                collision = false
            )
        )
    }
}