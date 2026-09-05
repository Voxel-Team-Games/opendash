package com.voxelteamgames.morphjump.game

import com.voxelteamgames.morphjump.engine.graphics.SpriteRenderer
import com.voxelteamgames.morphjump.engine.graphics.Texture
import com.voxelteamgames.morphjump.engine.graphics.VectorRenderer
import com.voxelteamgames.morphjump.engine.graphics.VectorShape

class Editor(
    private val level: Level
) {

    fun update(
        deltaTime: Float,
        window: Long
    ) {
        // Por enquanto o editor ainda não possui
        // ferramentas de edição.
    }

    fun render(
        renderer: SpriteRenderer,
        vectorRenderer: VectorRenderer,
        textures: Map<String, Texture>,
        vectors: Map<String, VectorShape>
    ) {

        for (levelObject in level.objects) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            when (definition.type) {

                ObjectType.BLOCK -> {

                    if (
                        definition.texture != null
                    ) {

                        val texture =
                            textures[
                                definition.texture
                            ]
                                ?: continue

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            64f * levelObject.scaleX,
                            64f * levelObject.scaleY
                        )

                    } else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ]
                                ?: continue

                        vectorRenderer.draw(
                            shape = shape,
                            x = levelObject.x,
                            y = levelObject.y,
                            scaleX =
                                64f * levelObject.scaleX,
                            scaleY =
                                64f * levelObject.scaleY,
                            rotation =
                                levelObject.rotation,
                            colorR = 1f,
                            colorG = 1f,
                            colorB = 1f,
                            colorA = 1f
                        )
                    }
                }

                ObjectType.HAZARD -> {

                    if (
                        definition.texture != null
                    ) {

                        val texture =
                            textures[
                                definition.texture
                            ]
                                ?: continue

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            64f * levelObject.scaleX,
                            64f * levelObject.scaleY
                        )

                    } else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ]
                                ?: continue

                        vectorRenderer.draw(
                            shape = shape,
                            x = levelObject.x,
                            y = levelObject.y,
                            scaleX =
                                64f * levelObject.scaleX,
                            scaleY =
                                64f * levelObject.scaleY,
                            rotation =
                                levelObject.rotation,
                            colorR = 1f,
                            colorG = 1f,
                            colorB = 1f,
                            colorA = 1f
                        )
                    }
                }

                ObjectType.DECO -> {

                    val vectorPath =
                        definition.vector
                            ?: continue

                    val shape =
                        vectors[vectorPath]
                            ?: continue

                    vectorRenderer.draw(
                        shape = shape,
                        x = levelObject.x,
                        y = levelObject.y,
                        scaleX =
                            64f * levelObject.scaleX,
                        scaleY =
                            64f * levelObject.scaleY,
                        rotation =
                            levelObject.rotation,
                        colorR = 1f,
                        colorG = 1f,
                        colorB = 1f,
                        colorA = 1f
                    )
                }

                ObjectType.TOUCH_TRIGGER -> {
                    // Touch triggers ainda não possuem
                    // comportamento visual específico.
                }
            }
        }
    }
}