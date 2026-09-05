package com.voxelteamgames.morphjump.game

data class Tile(
    val asset: String,

    var hasCollision: Boolean = false,
    var showTexture: Boolean = true,

    var direction: Float = 0f,

    var scaleX: Float = 1f,
    var scaleY: Float = 1f,

    var colorId: Int = 0,

    val positionX: Double = 0.0,
    val positionY: Double = 0.0,
    val positionZ: Short = 0,

    var offsetX: Double = 0.0,
    var offsetY: Double = 0.0
) {
    val renderX: Double
        get() = positionX + offsetX

    val renderY: Double
        get() = positionY + offsetY
}