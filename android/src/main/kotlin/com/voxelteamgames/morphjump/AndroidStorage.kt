package com.voxelteamgames.morphjump

import android.os.Environment
import java.io.File

object AndroidStorage {

    private const val ROOT_DIRECTORY = "morphjump"
    private const val LEVEL_DIRECTORY = "levels"

    fun levelsDirectory(): File {

        val storage =
            Environment.getExternalStorageDirectory()

        val morphjumpDirectory =
            File(
                storage,
                ROOT_DIRECTORY
            )

        val levelsDirectory =
            File(
                morphjumpDirectory,
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