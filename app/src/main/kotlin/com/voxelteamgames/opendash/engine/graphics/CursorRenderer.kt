package com.voxelteamgames.opendash.engine.graphics

import com.voxelteamgames.opendash.engine.input.MouseDevice
import org.lwjgl.opengl.GL11.*

class CursorRenderer {

    fun render(mouse: MouseDevice) {
        val x = mouse.x
        val y = mouse.y

        val size = 10.0

        glColor3f(1.0f, 1.0f, 1.0f)

        glBegin(GL_QUADS)

        glVertex2d(x - size, y - size)
        glVertex2d(x + size, y - size)
        glVertex2d(x + size, y + size)
        glVertex2d(x - size, y + size)

        glEnd()
    }
}