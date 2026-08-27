package com.voxelteamgames.opendash.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object LevelSaver {

    private val json =
        Json {
            prettyPrint = true
        }

    fun save(
        level: Level,
        path: String
    ) {

        val objects =
            level.objects.map { objectData ->

                LevelObjectData(
                    id = objectData.id,
                    x = objectData.x,
                    y = objectData.y,
                    scaleX = objectData.scaleX,
                    scaleY = objectData.scaleY,
                    rotation = objectData.rotation
                )
            }

        val data =
            LevelData(
                objects = objects
            )

        val file =
            File(path)

        file.parentFile?.mkdirs()

        file.writeText(
            json.encodeToString(data)
        )
    }

    @Serializable
    private data class LevelData(

        val objects: List<LevelObjectData>

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
}