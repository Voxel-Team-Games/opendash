package com.voxelteamgames.opendash.game

import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import com.voxelteamgames.opendash.engine.graphics.VectorRenderer
import com.voxelteamgames.opendash.engine.graphics.VectorShape
import com.voxelteamgames.opendash.engine.audio.MusicManager

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class Game(
    private val playerTexture: Texture,
    private var level: Level
) {

    var number = 1

    var nextLevelRequested = false
        private set

    var justRespawned = false
        private set

    private val player =
        Player(
            x = SPAWN_X,
            y = SPAWN_Y
        )

    val musicManager =
        MusicManager()

    // =================================================
    // INPUT
    // =================================================

    private var jumpWasPressed = false

    // =================================================
    // YELLOW ORB
    // =================================================

    /*
     * Indica se o jogador está atualmente encostando
     * em um Yellow Orb.
     */
    private var touchingYellowOrb = false

    /*
     * Cada contato com um orb fornece apenas um pulo
     * extra.
     *
     * Quando o jogador sai do orb, essa permissão é
     * removida. Ao tocar novamente, recebe outra.
     */
    private var yellowOrbJumpUsed = false

    // =================================================
    // MORTE
    // =================================================

    private var deathTimer = 0f

    companion object {

        // =================================================
        // MORTE
        // =================================================

        const val RESPAWN_DELAY = 1f

        // =================================================
        // SPAWN
        // =================================================

        const val SPAWN_X = 100f
        const val SPAWN_Y = 576f

        // =================================================
        // CHÃO
        // =================================================

        const val GROUND_Y = 640f

        // =================================================
        // MORTE POR QUEDA
        // =================================================

        const val DEATH_Y = 800f

        // =================================================
        // TOLERÂNCIA DE COLISÃO
        // =================================================

        const val TOP_TOLERANCE = 8f

        const val WALL_LETHALITY_MARGIN = 16f
    }

    // =================================================
    // PLAYER POSITION
    // =================================================

    val playerX: Float
        get() = player.x

    val playerY: Float
        get() = player.y

    val playerDead: Boolean
        get() = player.dead

    // =================================================
    // GAMEMODE
    // =================================================

    var playerGamemode: PlayerGamemode
        get() = player.gamemode
        set(value) {

            if (
                player.gamemode ==
                value
            ) {
                return
            }

            player.gamemode =
                value

            player.velocityY = 0f
            player.grounded = false
        }

    // =================================================
    // UPDATE
    // =================================================

    fun update(
        deltaTime: Float,
        window: Long
    ) {

        // =================================================
        // MORTE / RESPAWN
        // =================================================

        justRespawned = false

        if (
            player.dead
        ) {

            deathTimer +=
                deltaTime

            if (
                deathTimer >=
                RESPAWN_DELAY
            ) {

                player.reset(
                    SPAWN_X,
                    SPAWN_Y
                )

                deathTimer = 0f

                justRespawned = true

                touchingYellowOrb = false
                yellowOrbJumpUsed = false
                jumpWasPressed = false
            }

            return
        }

        // =================================================
        // DETECTAR YELLOW ORB
        // =================================================

        touchingYellowOrb =
            isTouchingYellowOrb()

        /*
         * Se saiu do orb, o pulo daquele contato deixa
         * de estar disponível.
         *
         * Quando entrar novamente em outro orb, ele será
         * liberado novamente.
         */
        if (
            !touchingYellowOrb
        ) {

            yellowOrbJumpUsed = false
        }

        // =================================================
        // INPUT
        // =================================================

        val jumpPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.SPACE
            ) ||
            Gdx.input.isButtonPressed(
                Input.Buttons.LEFT
            )

        /*
         * Detecta somente o momento em que o botão foi
         * pressionado.
         *
         * Isso é importante para o Yellow Orb: segurar
         * espaço não pode ativá-lo várias vezes.
         */
        val jumpJustPressed =
            jumpPressed

        // =================================================
        // CUBE INPUT
        // =================================================

        if (
            player.gamemode ==
            PlayerGamemode.CUBE
        ) {

            // ---------------------------------------------
            // PULO NORMAL
            // ---------------------------------------------

            if (
                jumpJustPressed &&
                player.grounded
            ) {

                player.jump()
            }

            // ---------------------------------------------
            // PULO DO YELLOW ORB
            // ---------------------------------------------

            else if (
                jumpJustPressed &&
                !player.grounded &&
                touchingYellowOrb &&
                !yellowOrbJumpUsed
            ) {

                /*
                 * O orb permite o segundo pulo.
                 *
                 * Primeiro marcamos como usado para
                 * impedir múltiplas ativações enquanto
                 * o jogador continua dentro dele.
                 */
                yellowOrbJumpUsed = true

                player.forceJump()
            }
        }

        jumpWasPressed =
            jumpPressed

        // =================================================
        // POSIÇÃO ANTERIOR
        // =================================================

        val previousBottom =
            player.y +
            player.height

        // =================================================
        // FÍSICA
        // =================================================

        player.update(
            deltaTime,
            jumpPressed
        )

        val currentLeft =
            player.x

        val currentRight =
            player.x +
            player.width

        val currentTop =
            player.y

        val currentBottom =
            player.y +
            player.height

        player.grounded = false

        // =================================================
        // CHÃO
        // =================================================

        if (
            player.gamemode ==
            PlayerGamemode.CUBE
        ) {

            if (
                currentBottom >=
                GROUND_Y &&
                player.velocityY >= 0f
            ) {

                player.landOn(
                    GROUND_Y
                )
            }
        }

        // =================================================
        // OBJETOS DA FASE
        // =================================================

        for (
            levelObject in
            level.objects
        ) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            val objectLeft =
                levelObject.x

            val objectRight =
                levelObject.x +
                64f *
                levelObject.scaleX

            val objectTop =
                levelObject.y

            val objectBottom =
                levelObject.y +
                64f *
                levelObject.scaleY

            // =================================================
            // TOUCH TRIGGER
            // =================================================

            if (
                definition.type ==
                ObjectType.TOUCH_TRIGGER
            ) {

                val horizontalCollision =
                    currentRight > objectLeft &&
                    currentLeft < objectRight

                val verticalCollision =
                    currentBottom > objectTop &&
                    currentTop < objectBottom

                if (
                    horizontalCollision &&
                    verticalCollision
                ) {

                    when (
                        levelObject.id
                    ) {

                        // -----------------------------------------
                        // YELLOW ORB
                        // -----------------------------------------

                        "trigger.yellow_orb" -> {

                            /*
                             * O estado de contato já é detectado
                             * no início do próximo frame, mas
                             * mantemos a identificação aqui também.
                             */
                            touchingYellowOrb = true
                        }

                        // -----------------------------------------
                        // PORTAL → CUBE
                        // -----------------------------------------

                        "trigger.portal_cube" -> {

                            playerGamemode =
                                PlayerGamemode.CUBE
                        }

                        // -----------------------------------------
                        // PORTAL → SHIP
                        // -----------------------------------------

                        "trigger.portal_ship" -> {

                            playerGamemode =
                                PlayerGamemode.SHIP
                        }

                        // -----------------------------------------
                        // TRIGGER END
                        // -----------------------------------------

                        "trigger.end" -> {

                            nextLevelRequested = true

                            return
                        }
                    }
                }

                continue
            }

            // =================================================
            // OBJETOS SEM COLISÃO
            // =================================================

            if (
                !definition.collision
            ) {
                continue
            }

            // =================================================
            // HAZARD
            // =================================================

            if (
                definition.type ==
                ObjectType.HAZARD
            ) {

                val horizontalCollision =
                    currentRight >
                    objectLeft + 21f &&
                    currentLeft <
                    objectRight - 21f

                val verticalCollision =
                    currentBottom >
                    objectTop + 21f &&
                    currentTop <
                    objectBottom

                if (
                    horizontalCollision &&
                    verticalCollision
                ) {

                    killPlayer()

                    return
                }
            }

            // =================================================
            // BLOCK
            // =================================================

            if (
                definition.type ==
                ObjectType.BLOCK
            ) {

                val horizontalCollision =
                    currentRight >
                    objectLeft &&
                    currentLeft <
                    objectRight

                val verticalCollision =
                    currentBottom >
                    objectTop &&
                    currentTop <
                    objectBottom

                if (
                    !horizontalCollision ||
                    !verticalCollision
                ) {
                    continue
                }

                // =================================================
                // TOLERÂNCIA DO TOPO
                // =================================================

                val topDistance =
                    currentBottom -
                    objectTop

                val landedOnTop =
                    previousBottom <=
                    objectTop &&
                    currentBottom >=
                    objectTop &&
                    player.velocityY >= 0f

                val nearTop =
                    topDistance >=
                    -TOP_TOLERANCE &&
                    topDistance <=
                    TOP_TOLERANCE

                // =================================================
                // POUSO NORMAL
                // =================================================

                if (
                    player.velocityY >= 0f &&
                    (
                        landedOnTop ||
                        nearTop
                    )
                ) {

                    player.landOn(
                        objectTop
                    )

                    continue
                }

                // =================================================
                // COLISÃO LETAL
                // =================================================

                val deeplyInsideBlock =
                    topDistance >
                    WALL_LETHALITY_MARGIN

                if (
                    deeplyInsideBlock
                ) {

                    killPlayer()

                    return
                }

                // =================================================
                // MARGEM TOLERÁVEL
                // =================================================

                if (
                    player.velocityY >= 0f &&
                    topDistance <=
                    WALL_LETHALITY_MARGIN
                ) {

                    player.landOn(
                        objectTop
                    )

                    continue
                }

                killPlayer()

                return
            }
        }

        // =================================================
        // CAIU DA FASE
        // =================================================

        if (
            player.y >
            DEATH_Y
        ) {

            killPlayer()

            return
        }
    }

    // =================================================
    // YELLOW ORB COLLISION
    // =================================================

    private fun isTouchingYellowOrb(): Boolean {

        val currentLeft =
            player.x

        val currentRight =
            player.x +
            player.width

        val currentTop =
            player.y

        val currentBottom =
            player.y +
            player.height

        for (
            levelObject in
            level.objects
        ) {

            if (
                levelObject.id !=
                "trigger.yellow_orb"
            ) {
                continue
            }

            val objectLeft =
                levelObject.x

            val objectRight =
                levelObject.x +
                64f *
                levelObject.scaleX

            val objectTop =
                levelObject.y

            val objectBottom =
                levelObject.y +
                64f *
                levelObject.scaleY

            val horizontalCollision =
                currentRight > objectLeft &&
                currentLeft < objectRight

            val verticalCollision =
                currentBottom > objectTop &&
                currentTop < objectBottom

            if (
                horizontalCollision &&
                verticalCollision
            ) {

                return true
            }
        }

        return false
    }

    // =================================================
    // MORTE
    // =================================================

    private fun killPlayer() {

        player.kill()

        deathTimer = 0f

        touchingYellowOrb = false
        yellowOrbJumpUsed = false
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

        nextLevelRequested = false

        touchingYellowOrb = false
        yellowOrbJumpUsed = false
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

        // =================================================
        // OBJETOS DA FASE
        // =================================================

        for (
            levelObject in
            level.objects
        ) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            when (
                definition.type
            ) {

                // =================================================
                // BLOCK
                // =================================================

                ObjectType.BLOCK -> {

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
                            64f *
                                levelObject.scaleX,
                            64f *
                                levelObject.scaleY
                        )

                    } else if (
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
                                64f *
                                levelObject.scaleX,
                            scaleY =
                                64f *
                                levelObject.scaleY,
                            rotation =
                                levelObject.rotation,
                            colorR = 1f,
                            colorG = 1f,
                            colorB = 1f,
                            colorA = 1f
                        )
                    }
                }

                // =================================================
                // HAZARD
                // =================================================

                ObjectType.HAZARD -> {

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
                            64f *
                                levelObject.scaleX,
                            64f *
                                levelObject.scaleY
                        )

                    } else if (
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
                                64f *
                                levelObject.scaleX,
                            scaleY =
                                64f *
                                levelObject.scaleY,
                            rotation =
                                levelObject.rotation,
                            colorR = 1f,
                            colorG = 1f,
                            colorB = 1f,
                            colorA = 1f
                        )
                    }
                }

                // =================================================
                // DECO
                // =================================================

                ObjectType.DECO -> {

                    val vectorPath =
                        definition.vector
                            ?: continue

                    val shape =
                        vectors[
                            vectorPath
                        ]
                            ?: error(
                                "Vetor não carregado: " +
                                vectorPath
                            )

                    vectorRenderer.draw(
                        shape = shape,
                        x = levelObject.x,
                        y = levelObject.y,
                        scaleX =
                            64f *
                            levelObject.scaleX,
                        scaleY =
                            64f *
                            levelObject.scaleY,
                        rotation =
                            levelObject.rotation,
                        colorR = 1f,
                        colorG = 1f,
                        colorB = 1f,
                        colorA = 1f
                    )
                }

                // =================================================
                // TOUCH TRIGGER
                // =================================================

                ObjectType.TOUCH_TRIGGER -> {

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
                            64f *
                                levelObject.scaleX,
                            64f *
                                levelObject.scaleY
                        )

                    } else if (
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
                                64f *
                                levelObject.scaleX,
                            scaleY =
                                64f *
                                levelObject.scaleY,
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

        // =================================================
        // PLAYER
        // =================================================

        if (
            player.dead
        ) {
            return
        }

        renderer.draw(
            playerTexture,
            player.x,
            player.y,
            player.width,
            player.height
        )
    }

    fun clearNextLevelRequest() {
        nextLevelRequested = false
    }
}