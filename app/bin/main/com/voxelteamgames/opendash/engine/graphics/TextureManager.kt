package com.voxelteamgames.opendash.engine.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*

class TextureManager {

    private val textures = mutableMapOf<String, Texture>()

    fun load(name: String, path: String): Texture {
        val resource = TextureManager::class.java.getResourceAsStream(path)
            ?: error("Textura não encontrada: $path")

        val bytes = resource.readBytes()

        val imageBuffer = BufferUtils.createByteBuffer(bytes.size)
        imageBuffer.put(bytes)
        imageBuffer.flip()

        val widthBuffer = BufferUtils.createIntBuffer(1)
        val heightBuffer = BufferUtils.createIntBuffer(1)
        val channelsBuffer = BufferUtils.createIntBuffer(1)

        val image = stbi_load_from_memory(
            imageBuffer,
            widthBuffer,
            heightBuffer,
            channelsBuffer,
            4
        ) ?: error(
            "Não foi possível carregar textura $path: ${stbi_failure_reason()}"
        )

        val width = widthBuffer[0]
        val height = heightBuffer[0]

        val textureId = glGenTextures()

        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_MIN_FILTER,
            GL_NEAREST
        )

        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_MAG_FILTER,
            GL_NEAREST
        )

        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_S,
            GL_CLAMP
        )

        glTexParameteri(
            GL_TEXTURE_2D,
            GL_TEXTURE_WRAP_T,
            GL_CLAMP
        )

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            width,
            height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            image
        )

        stbi_image_free(image)

        glBindTexture(GL_TEXTURE_2D, 0)

        val texture = Texture(
            textureId,
            width,
            height
        )

        textures[name] = texture

        return texture
    }

    operator fun get(name: String): Texture {
        return textures[name]
            ?: error("Textura não carregada: $name")
    }

    fun destroy() {
        textures.values.forEach {
            it.destroy()
        }

        textures.clear()
    }
}