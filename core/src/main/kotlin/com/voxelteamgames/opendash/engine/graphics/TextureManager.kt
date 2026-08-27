package com.voxelteamgames.opendash.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.Texture.TextureWrap

class TextureManager {

    private val textures =
        mutableMapOf<String, Texture>()

    fun load(
        name: String,
        path: String
    ): Texture {

        val file =
            Gdx.files.internal(
                path.removePrefix("/")
            )

if (!file.exists()) {
    error(
        """
        Textura não encontrada!
        Original: $path
        Normalizado: ${path.removePrefix("/")}
        Path: ${file.path()}
        Type: ${file.type()}
        Exists: ${file.exists()}
        Absolute: ${file.file().absolutePath}
        """.trimIndent()
    )
}

        val gdxTexture =
            com.badlogic.gdx.graphics.Texture(
                file
            )

        gdxTexture.setFilter(
            TextureFilter.Nearest,
            TextureFilter.Nearest
        )

        gdxTexture.setWrap(
            TextureWrap.ClampToEdge,
            TextureWrap.ClampToEdge
        )

        val texture =
            Texture(
                gdxTexture,
                gdxTexture.width,
                gdxTexture.height
            )

        textures[name] =
            texture

        return texture
    }

    operator fun get(
        name: String
    ): Texture {

        return textures[name]
            ?: error(
                "Textura não carregada: $name"
            )
    }

    fun destroy() {

        textures.values.forEach {
            it.destroy()
        }

        textures.clear()
    }
}