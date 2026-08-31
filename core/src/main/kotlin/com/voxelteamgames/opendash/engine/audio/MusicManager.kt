package com.voxelteamgames.opendash.engine.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
class MusicManager {

    private var music: Music? = null
    private var currentUrl: String? = null

    fun play(path: String?) {
        if (path.isNullOrBlank()) {
            stop()
            return
        }

        if (currentUrl == path && music?.isPlaying == true) {
            return
        }

        stop()

        val newMusic = Gdx.audio.newMusic(
            Gdx.files.internal(path)
        )

        newMusic.isLooping = true
        newMusic.play()

        music = newMusic
        currentUrl = path

        println("Música iniciada: $path")
    }

fun restart() {

    val currentMusic =
        music
            ?: return

    currentMusic.stop()
    currentMusic.setPosition(0f)
    currentMusic.play()

    println(
        "Música reiniciada"
    )
}

    fun pause() {
        music?.pause()
    }

    fun stop() {
        music?.stop()
        music?.dispose()

        music = null
        currentUrl = null
    }

    fun destroy() {
        stop()
    }
}