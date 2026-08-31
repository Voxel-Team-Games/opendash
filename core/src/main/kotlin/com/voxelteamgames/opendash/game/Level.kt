package com.voxelteamgames.opendash.game

data class Level(
    val objects: MutableList<LevelObject>,
    var music: String? = null,
    var speed: Float = 350f
)