package com.voxelteamgames.opendash.game

import java.io.File

object LevelStorage {

    private const val LEVEL_DIRECTORY =
        "app/levels"

    fun save(
        level: Level,
        name: String
    ) {

        val file =
            File(
                LEVEL_DIRECTORY,
                normalizeName(name)
            )

        LevelSaver.save(
            level,
            file.path
        )

        println(
            "Fase salva em:"
        )

        println(
            file.absolutePath
        )
    }

    fun exists(
        name: String
    ): Boolean {

        return File(
            LEVEL_DIRECTORY,
            normalizeName(name)
        ).exists()
    }

    fun load(
        name: String
    ): Level {

        val file =
            File(
                LEVEL_DIRECTORY,
                normalizeName(name)
            )

        if (file.exists()) {

            println(
                "Carregando fase editada:"
            )

            println(
                file.absolutePath
            )

            return LevelParser.loadFile(
                file
            )
        }

        println(
            "Fase editada não encontrada."
        )

        println(
            "Procurando nos recursos..."
        )

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