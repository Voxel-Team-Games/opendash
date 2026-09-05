package com.voxelteamgames.morphjump.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

object LevelParser {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun load(
        path: String
    ): Level {

        val stream =
            Thread.currentThread()
                .contextClassLoader
                .getResourceAsStream(path)
                ?: error(
                    "Level não encontrado: $path"
                )

        stream.use {

            val text =
                it.bufferedReader()
                    .use { reader ->
                        reader.readText()
                    }

            val data =
                json.decodeFromString<LevelData>(
                    text
                )

            val objects =
                data.objects.map { objectData ->

                    LevelObject(
                        id = objectData.id,
                        x = objectData.x,
                        y = objectData.y,
                        scaleX = objectData.scaleX,
                        scaleY = objectData.scaleY,
                        rotation = objectData.rotation
                    )

                }.toMutableList()

return Level(
    objects = objects,
    music = data.music,
    speed = data.speed
)
        }
    }

@Serializable
private data class LevelData(

    val music: String? = null,

    val speed: Float = 350f,

    val objects: List<LevelObjectData> = emptyList()

)

    @Serializable
    private data class LevelObjectData(

        val id: String,

        val x: Float,

        val y: Float,

        val scaleX: Float = 1f,

        val scaleY: Float = 1f,

        val rotation: Float = 0f

    )

    fun loadFile(
        file: File
    ): Level {

        val text =
            file.readText()

        val data =
            json.decodeFromString<LevelData>(
                text
            )

        val objects =
            data.objects.map { objectData ->

                LevelObject(
                    id = objectData.id,
                    x = objectData.x,
                    y = objectData.y,
                    scaleX = objectData.scaleX,
                    scaleY = objectData.scaleY,
                    rotation = objectData.rotation
                )

            }.toMutableList()

return Level(
    objects = objects,
    music = data.music,
    speed = data.speed
)
    }
}