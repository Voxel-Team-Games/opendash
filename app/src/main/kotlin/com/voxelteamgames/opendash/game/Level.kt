package com.voxelteamgames.opendash.game

data class Level(
    val objects: MutableList<LevelObject>,
    val music: String? = null
)