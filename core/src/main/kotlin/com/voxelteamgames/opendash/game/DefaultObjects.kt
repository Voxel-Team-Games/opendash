package com.voxelteamgames.opendash.game

object DefaultObjects {

    fun register() {

        ObjectRegistry.clear()

        // =================================================
        // BLOCK
        // =================================================

        ObjectRegistry.register(
            ObjectDefinition(
                id = "block.iron",
                type = ObjectType.BLOCK,
                texture =
                    "/textures/blocks/default/raster_iron_block.png",
                collision = true
            )
        )

        // =================================================
        // HAZARDS
        // =================================================

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

        // =================================================
        // DECO
        // =================================================

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

        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.jump_marker_2",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/jump_marker_2.png",
                collision = true
            )
        )
        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.jump_marker",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/jump_marker.png",
                collision = true
            )
        )

        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.back_iron_block",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/raster_iron_block_back.png",
                collision = true
            )
        )
        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.back_yellow_spike",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/raster_yellow_spike_back.png",
                collision = true
            )
        )
        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.white_square",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/white_square.png",
                collision = true
            )
        )
        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.gray_square",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/gray_square.png",
                collision = true
            )
        )
        ObjectRegistry.register(
            ObjectDefinition(
                id = "deco.black_square",
                type = ObjectType.TOUCH_TRIGGER,
                texture =
                    "/textures/deco/black_square.png",
                collision = true
            )
        )

// =================================================
// TOUCH TRIGGERS
// =================================================

ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.portal_cube",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/deco/cube_portal.png",
        collision = true
    )
)

ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.portal_ship",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/deco/ship_portal.png",
        collision = true
    )
)

ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.end",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/deco/finish.png",
        collision = true
    )
)
ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.yellow_orb",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/deco/yellow_orb.png",
        collision = true
    )
)
ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.invert",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/trigger/invert_trigger.png",
        collision = true,
        trigger = TouchTriggerType.GRAVITY_INVERT
    )
)

ObjectRegistry.register(
    ObjectDefinition(
        id = "trigger.reverse",
        type = ObjectType.TOUCH_TRIGGER,
        texture = "/textures/trigger/reverse_trigger.png",
        collision = true,
        trigger = TouchTriggerType.DIRECTION_REVERSE
    )
)
    }
}