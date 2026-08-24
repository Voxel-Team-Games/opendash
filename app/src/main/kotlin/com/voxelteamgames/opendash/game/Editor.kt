package com.voxelteamgames.opendash.game

import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import com.voxelteamgames.opendash.engine.graphics.VectorRenderer
import com.voxelteamgames.opendash.engine.graphics.VectorShape
import org.lwjgl.glfw.GLFW.*

class Editor(
    private val level: Level
) {

    // =================================================
    // UPDATE
    // =================================================

    fun update(
        deltaTime: Float,
        window: Long
    ) {

        // Por enquanto o editor ainda não possui
        // ferramentas de edição.
        //
        // Elas entrarão aqui:
        //
        // - seleção
        // - movimentação
        // - criação
        // - remoção
        // - câmera
        // etc.
    }

    // =================================================
    // RENDER
    // =================================================

    fun render(
        renderer: SpriteRenderer,
        vectorRenderer: VectorRenderer,
        textures: Map<String, Texture>,
        vectors: Map<String, VectorShape>
    ) {

        // O editor inicialmente desenha a própria fase.
        //
        // Depois adicionaremos:
        //
        // - grade
        // - seleção
        // - gizmos
        // - cursor
        // - ferramentas
        // - UI

        for (levelObject in level.objects) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            when (definition.type) {

                // =============================================
                // BLOCK
                // =============================================

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
                    }

                    else if (
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

                // =============================================
                // HAZARD
                // =============================================

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
                    }

                    else if (
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

                // =============================================
                // DECO
                // =============================================

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
            }
        }
    }
}