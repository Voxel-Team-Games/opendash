package com.voxelteamgames.opendash.engine.graphics

import com.badlogic.gdx.graphics.g2d.SpriteBatch

class SpriteRenderer {

    private val batch =
        SpriteBatch()

    private var camera: Camera? = null

    private var screenWidth = 0
    private var screenHeight = 0

    fun begin(
        width: Int,
        height: Int,
        camera: Camera? = null
    ) {

        screenWidth = width
        screenHeight = height

        this.camera = camera

        batch.begin()
    }

    fun draw(
        texture: Texture,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        rotation: Float = 0f
    ) {

        val camera =
            this.camera

        val screenX: Float
        val screenY: Float
        val drawWidth: Float
        val drawHeight: Float

        if (camera != null) {

            screenX =
                (x - camera.x) * camera.zoom +
                screenWidth / 2f

            drawWidth =
                width * camera.zoom

            drawHeight =
                height * camera.zoom

            val worldScreenY =
                (y - camera.y) * camera.zoom +
                screenHeight / 2f

            screenY =
                screenHeight -
                worldScreenY -
                drawHeight

        } else {

            screenX =
                x

            drawWidth =
                width

            drawHeight =
                height

            screenY =
                screenHeight -
                y -
                height
        }

        batch.draw(
            texture.gdxTexture,
            screenX,
            screenY,
            drawWidth / 2f,
            drawHeight / 2f,
            drawWidth,
            drawHeight,
            1f,
            1f,
            rotation,
            0,
            0,
            texture.gdxTexture.width,
            texture.gdxTexture.height,
            false,
            false
        )
    }

    fun end() {

        batch.end()

        camera = null
    }

    fun dispose() {

        batch.dispose()
    }
}