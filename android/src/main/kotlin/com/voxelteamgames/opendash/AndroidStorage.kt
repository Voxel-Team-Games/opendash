package com.voxelteamgames.opendash

import android.os.Environment
import java.io.File

object AndroidStorage {

    private const val ROOT_DIRECTORY = "OpenDash"
    private const val LEVEL_DIRECTORY = "levels"

    fun levelsDirectory(): File {

        val storage =
            Environment.getExternalStorageDirectory()

        val openDashDirectory =
            File(
                storage,
                ROOT_DIRECTORY
            )

        val levelsDirectory =
            File(
                openDashDirectory,
                LEVEL_DIRECTORY
            )

        if (!levelsDirectory.exists()) {
            levelsDirectory.mkdirs()
        }

        return levelsDirectory
    }

    fun hasStorageAccess(): Boolean {

        return Environment.isExternalStorageManager()
    }
}