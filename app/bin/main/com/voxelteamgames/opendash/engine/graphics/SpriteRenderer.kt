package com.voxelteamgames.opendash.engine.graphics

import org.lwjgl.opengl.GL11.*

class SpriteRenderer {

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

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()

        glOrtho(
            0.0,
            width.toDouble(),
            height.toDouble(),
            0.0,
            -1.0,
            1.0
        )

        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()

        glEnable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)

        glBlendFunc(
            GL_SRC_ALPHA,
            GL_ONE_MINUS_SRC_ALPHA
        )
    }

    fun draw(
        texture: Texture,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {

        val camera = this.camera

        val screenX: Float
        val screenY: Float
        val drawWidth: Float
        val drawHeight: Float

        if (camera != null) {

            screenX =
                (x - camera.x) * camera.zoom +
                screenWidth / 2f

            screenY =
                (y - camera.y) * camera.zoom +
                screenHeight / 2f

            drawWidth = width * camera.zoom
            drawHeight = height * camera.zoom

        } else {

            screenX = x
            screenY = y

            drawWidth = width
            drawHeight = height
        }

        texture.bind()

        glBegin(GL_QUADS)

        glTexCoord2f(0f, 0f)
        glVertex2f(
            screenX,
            screenY
        )

        glTexCoord2f(1f, 0f)
        glVertex2f(
            screenX + drawWidth,
            screenY
        )

        glTexCoord2f(1f, 1f)
        glVertex2f(
            screenX + drawWidth,
            screenY + drawHeight
        )

        glTexCoord2f(0f, 1f)
        glVertex2f(
            screenX,
            screenY + drawHeight
        )

        glEnd()

        texture.unbind()
    }

    fun end() {

        glDisable(GL_BLEND)
        glDisable(GL_TEXTURE_2D)

        camera = null
    }
}