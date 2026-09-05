package com.voxelteamgames.morphjump

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {

    val config =
        Lwjgl3ApplicationConfiguration().apply {

            setTitle("MorphJump")

            setWindowedMode(
                1280,
                720
            )

            useVsync(true)
        }

    Lwjgl3Application(
        morphjumpGame(),
        config
    )
}