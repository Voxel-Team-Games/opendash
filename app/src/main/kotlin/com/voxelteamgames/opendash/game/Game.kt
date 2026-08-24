package com.voxelteamgames.opendash.game

import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import com.voxelteamgames.opendash.engine.graphics.VectorRenderer
import com.voxelteamgames.opendash.engine.graphics.VectorShape
import org.lwjgl.glfw.GLFW.*

class Game(
    private val playerTexture: Texture,
    private val level: Level
) {

    private val player = Player(
        x = SPAWN_X,
        y = SPAWN_Y
    )

    private var jumpWasPressed = false
    private var deathTimer = 0f

    companion object {

        const val RESPAWN_DELAY = 1f

        const val SPAWN_X = 100f
        const val SPAWN_Y = 576f

        const val GROUND_Y = 640f
        const val DEATH_Y = 800f
    }

    val playerX: Float
        get() = player.x

    val playerY: Float
        get() = player.y

    // =================================================
    // UPDATE
    // =================================================

    fun update(
        deltaTime: Float,
        window: Long
    ) {

        // ---------------------------------------------
        // MORTE / RESPAWN
        // ---------------------------------------------

        if (player.dead) {

            deathTimer += deltaTime

            if (deathTimer >= RESPAWN_DELAY) {

                player.reset(
                    SPAWN_X,
                    SPAWN_Y
                )

                deathTimer = 0f
            }

            return
        }

        // ---------------------------------------------
        // INPUT
        // ---------------------------------------------

        val jumpPressed =
            glfwGetKey(
                window,
                GLFW_KEY_SPACE
            ) == GLFW_PRESS ||
            glfwGetMouseButton(
                window,
                GLFW_MOUSE_BUTTON_LEFT
            ) == GLFW_PRESS

        if (
            jumpPressed
        ) {
            player.jump()
        }

        jumpWasPressed = jumpPressed

        // ---------------------------------------------
        // POSIÇÃO ANTERIOR
        // ---------------------------------------------

        val previousX = player.x
        val previousY = player.y

        val previousLeft = player.x
        val previousRight =
            player.x + player.width

        val previousTop = player.y

        val previousBottom =
            player.y + player.height

        // ---------------------------------------------
        // FÍSICA
        // ---------------------------------------------

        player.update(deltaTime)

        val currentLeft = player.x
        val currentRight =
            player.x + player.width

        val currentTop = player.y
        val currentBottom =
            player.y + player.height

        player.grounded = false

        // =================================================
        // CHÃO
        // =================================================

        if (
            currentBottom >= GROUND_Y &&
            player.velocityY >= 0f
        ) {

            player.landOn(GROUND_Y)
        }

        // =================================================
        // COLISÕES COM OBJETOS
        // =================================================

        for (levelObject in level.objects) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            if (!definition.collision) {
                continue
            }

            val objectLeft =
                levelObject.x

            val objectRight =
                levelObject.x +
                64f * levelObject.scaleX

            val objectTop =
                levelObject.y

            val objectBottom =
                levelObject.y +
                64f * levelObject.scaleY

            // ---------------------------------------------
            // HAZARD
            // ---------------------------------------------

            if (
                definition.type ==
                ObjectType.HAZARD
            ) {

                val horizontalCollision =
                    currentRight > objectLeft + 21 &&
                    currentLeft < objectRight - 21

                val verticalCollision =
                    currentBottom > objectTop + 21 &&
                    currentTop < objectBottom

                if (
                    horizontalCollision &&
                    verticalCollision
                ) {

                    killPlayer()

                    return
                }
            }

            // ---------------------------------------------
            // BLOCK
            // ---------------------------------------------

            if (
                definition.type ==
                ObjectType.BLOCK
            ) {

                val horizontalCollision =
                    currentRight > objectLeft &&
                    currentLeft < objectRight

                val verticalCollision =
                    currentBottom > objectTop &&
                    currentTop < objectBottom

                if (
                    !horizontalCollision ||
                    !verticalCollision
                ) {
                    continue
                }

                // =================================================
                // 1. TOPO DO BLOCO
                // =================================================
                //
                // Se o jogador estava acima do bloco e caiu
                // através do topo, ele pousa normalmente.
                //

                val landedOnTop =
                    previousBottom <= objectTop &&
                    currentBottom >= objectTop &&
                    player.velocityY >= 0f

                if (landedOnTop) {

                    player.landOn(objectTop)

                    continue
                }

                // =================================================
                // 2. QUALQUER OUTRA PARTE DO BLOCO = MORTE
                // =================================================
                //
                // Isso inclui:
                //
                // - lateral esquerda
                // - lateral direita
                // - parte inferior
                // - entrar no bloco por qualquer outro ângulo
                //

                killPlayer()

                return
            }
        }

        // =================================================
        // CAIU DA FASE
        // =================================================

        if (player.y > DEATH_Y) {

            killPlayer()

            return
        }
    }

    // =================================================
    // MORTE
    // =================================================

    private fun killPlayer() {

        player.kill()
        deathTimer = 0f
    }

    // =================================================
    // RESTART
    // =================================================

    fun restart() {

        player.reset(
            SPAWN_X,
            SPAWN_Y
        )

        deathTimer = 0f
        jumpWasPressed = false
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

        if (player.dead) {
            return
        }

        // =================================================
        // OBJETOS DA FASE
        // =================================================

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

                    // -----------------------------------------
                    // BLOCK COM TEXTURA
                    // -----------------------------------------

                    if (
                        definition.texture != null
                    ) {

                        val texture =
                            textures[
                                definition.texture
                            ]
                                ?: error(
                                    "Textura não carregada: " +
                                    definition.texture
                                )

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            64f * levelObject.scaleX,
                            64f * levelObject.scaleY
                        )
                    }

                    // -----------------------------------------
                    // BLOCK VETORIAL
                    // -----------------------------------------

                    else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ]
                                ?: error(
                                    "Vetor não carregado: " +
                                    definition.vector
                                )

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

                    // -----------------------------------------
                    // HAZARD COM TEXTURA
                    // -----------------------------------------

                    if (
                        definition.texture != null
                    ) {

                        val texture =
                            textures[
                                definition.texture
                            ]
                                ?: error(
                                    "Textura não carregada: " +
                                    definition.texture
                                )

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            64f * levelObject.scaleX,
                            64f * levelObject.scaleY
                        )
                    }

                    // -----------------------------------------
                    // HAZARD VETORIAL
                    // -----------------------------------------

                    else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ]
                                ?: error(
                                    "Vetor não carregado: " +
                                    definition.vector
                                )

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
                            ?: error(
                                "Vetor não carregado: $vectorPath"
                            )

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

        // =================================================
        // PLAYER
        // =================================================

        renderer.draw(
            playerTexture,
            player.x,
            player.y,
            player.width,
            player.height
        )
    }
}