package com.voxelteamgames.opendash.game

import java.io.File

object LevelStorage {

    private const val DESKTOP_LEVEL_DIRECTORY =
        "levels"

    var provider: LevelDirectoryProvider? =
        null

    private fun levelDirectory(): File {

        val externalDirectory =
            provider?.getLevelsDirectory()

        return if (externalDirectory != null) {
            File(externalDirectory)
        } else {
            File(DESKTOP_LEVEL_DIRECTORY)
        }
    }

    fun save(
        level: Level,
        name: String
    ) {

        val directory =
            levelDirectory()

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file =
            File(
                directory,
                normalizeName(name)
            )

        LevelSaver.save(
            level,
            file.path
        )
    }

    fun exists(
        name: String
    ): Boolean {

        return File(
            levelDirectory(),
            normalizeName(name)
        ).exists()
    }

    fun load(
        name: String
    ): Level {

        val file =
            File(
                levelDirectory(),
                normalizeName(name)
            )

        if (file.exists()) {

            println("Carregando fase editada:")
            println(file.absolutePath)

            return LevelParser.loadFile(file)
        }

        println("Fase editada não encontrada.")
        println("Procurando nos recursos...")

        return LevelParser.load(
            "levels/${normalizeName(name)}"
        )
    }

    private fun normalizeName(
        name: String
    ): String {

        return if (
            name.endsWith(".json")
        ) {
            name
        } else {
            "$name.json"
        }
    }
}