package com.voxelteamgames.morphjump.engine.graphics

class Camera(
    var x: Float = 640f,
    var y: Float = 360f,
    var zoom: Float = 1f,

    var width: Float = 1280f,
    var height: Float = 720f
) {

    fun resize(
        width: Float,
        height: Float
    ) {

        this.width = width
        this.height = height
    }

    fun follow(
        targetX: Float,
        targetY: Float
    ) {

        x =
            targetX + 250f

        y =
            targetY
    }

    fun worldToScreenX(
        worldX: Float
    ): Float {

        return (
            worldX - x
        ) * zoom +
            width / 2f
    }

    fun worldToScreenY(
        worldY: Float
    ): Float {

        return (
            worldY - y
        ) * zoom +
            height / 2f
    }

    fun screenToWorldX(
        screenX: Float
    ): Float {

        return (
            screenX -
            width / 2f
        ) / zoom + x
    }

fun screenToWorldY(screenY: Float): Float {

    val morphjumpScreenY =
        height - screenY

    return (
        morphjumpScreenY -
        height / 2f
    ) / zoom + y
}
}