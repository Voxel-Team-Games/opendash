package com.voxelteamgames.opendash.engine.graphics

import org.lwjgl.opengl.GL11.*

class VectorRenderer {

    private var camera: Camera? = null

    fun begin(
        width: Int,
        height: Int,
        camera: Camera? = null
    ) {

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
    }

    fun draw(
        shape: VectorShape,
        x: Float,
        y: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float
    ) {

        val camera = this.camera

        glPushMatrix()

        if (camera != null) {

            val screenX =
                (x - camera.x) * camera.zoom +
                640f

            val screenY =
                (y - camera.y) * camera.zoom +
                360f

            glTranslatef(
                screenX,
                screenY,
                0f
            )

            glScalef(
                scaleX * camera.zoom,
                scaleY * camera.zoom,
                1f
            )

        } else {

            glTranslatef(
                x,
                y,
                0f
            )

            glScalef(
                scaleX,
                scaleY,
                1f
            )
        }

        glRotatef(
            rotation,
            0f,
            0f,
            1f
        )

        glColor4f(
            colorR,
            colorG,
            colorB,
            colorA
        )

        glBegin(GL_POLYGON)

        for (i in shape.vertices.indices step 2) {

            glVertex2f(
                shape.vertices[i],
                shape.vertices[i + 1]
            )
        }

        glEnd()

        glPopMatrix()

        glColor4f(
            1f,
            1f,
            1f,
            1f
        )
    }

    fun end() {
        camera = null
    }
}