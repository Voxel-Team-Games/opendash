package com.voxelteamgames.morphjump.game
data class LevelObject(
    val id: String,
    var x: Float,
    var y: Float,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var rotation: Float = 0f
)