package com.voxelteamgames.opendash.engine.graphics

import org.lwjgl.opengl.GL11.*

class Texture(
    val id: Int,
    val width: Int,
    val height: Int
) {

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun destroy() {
        glDeleteTextures(id)
    }
}