package com.voxelteamgames.opendash.engine.graphics

import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.Texture.TextureWrap

class Texture(
    val gdxTexture: com.badlogic.gdx.graphics.Texture,
    val width: Int,
    val height: Int
) {

    init {

        gdxTexture.setFilter(
            TextureFilter.Nearest,
            TextureFilter.Nearest
        )

        gdxTexture.setWrap(
            TextureWrap.ClampToEdge,
            TextureWrap.ClampToEdge
        )
    }

    fun destroy() {
        gdxTexture.dispose()
    }
}