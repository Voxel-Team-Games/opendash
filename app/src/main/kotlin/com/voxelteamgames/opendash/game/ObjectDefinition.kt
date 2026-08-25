package com.voxelteamgames.opendash.game

data class ObjectDefinition(

    val id: String,

    val type: ObjectType,

    val texture: String? = null,

    val vector: String? = null,

    val collision: Boolean = false,

    val trigger: TouchTriggerType? = null
)