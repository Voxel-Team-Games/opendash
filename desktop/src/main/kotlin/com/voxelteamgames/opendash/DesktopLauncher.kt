package com.voxelteamgames.opendash

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {

    val config =
        Lwjgl3ApplicationConfiguration().apply {

            setTitle("OpenDash")

            setWindowedMode(
                1280,
                720
            )

            useVsync(true)
        }

    Lwjgl3Application(
        OpenDashGame(),
        config
    )
}