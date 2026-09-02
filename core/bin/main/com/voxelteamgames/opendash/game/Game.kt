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
private var reverseTriggerUsed = false
private var invertTriggerUsed = false
    var justRespawned = false
        private set

    private val player =
        Player(
            x = SPAWN_X,
            y = SPAWN_Y
        )

    val musicManager =
        MusicManager()
init {
    musicManager.play(level.music)
}
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
        val touchingInvertTrigger =
    isTouchingInvertTrigger()

if (
    !touchingInvertTrigger
) {
    invertTriggerUsed = false
}
        // =================================================
        // MORTE / RESPAWN
        // =================================================

        justRespawned = false

        if (
            player.dead
        ) {
            musicManager.stop()
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

                musicManager.play(level.music)
                musicManager.restart()
            }

            return
        }
        if (!isTouchingReverseTrigger()) {
    reverseTriggerUsed = false
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
                !yellowOrbJumpUsed &&
            !jumpWasPressed
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

val previousTop =
    player.y

val previousCenterX =
    player.x +
    player.width / 2f

        val previousBottom =
            player.y +
            player.height

        // =================================================
        // FÍSICA
        // =================================================

        player.update(
            deltaTime,
            jumpPressed,
            level.speed
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

            // =================================================
            // TOUCH TRIGGER
            // =================================================

            if (
                definition.type ==
                ObjectType.TOUCH_TRIGGER
            ) {

if (
    isPlayerCollidingWithObject(
        levelObject
    )
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
// REVERSE TRIGGER
// -----------------------------------------

"trigger.reverse" -> {

    if (!reverseTriggerUsed) {

        player.reverseDirection()

        reverseTriggerUsed = true
    }
}

"trigger.invert" -> {

    if (!invertTriggerUsed) {

        player.gravityInverted =
            !player.gravityInverted

        invertTriggerUsed = true
    }
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

    if (
        isPlayerCollidingWithObject(
            levelObject
        )
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

    val colliding =
        isPlayerCollidingWithObject(
            levelObject
        )

    if (!colliding) {
        continue
    }

    // =================================================
    // GRAVIDADE NORMAL
    // =================================================

    if (
        !player.gravityInverted
    ) {

        /*
         * O player está caindo e atingiu
         * a parte superior do bloco.
         */
        if (
            player.velocityY >= 0f &&
            previousBottom <=
            levelObject.y +
            64f *
            levelObject.scaleY +
            TOP_TOLERANCE
        ) {

            player.landOn(
                levelObject.y
            )

            continue
        }

        // ---------------------------------------------
        // COLISÃO LATERAL / INFERIOR
        // ---------------------------------------------

        killPlayer()

        return
    }

    // =================================================
    // GRAVIDADE INVERTIDA
    // =================================================

    else {

        val blockBottom =
            levelObject.y +
            64f *
            levelObject.scaleY

        /*
         * O player está subindo e atingiu
         * a parte inferior do bloco.
         */
        if (
            player.velocityY <= 0f &&
            previousTop >=
            blockBottom -
            TOP_TOLERANCE
        ) {

            player.landOn(
                blockBottom
            )

            continue
        }

        // ---------------------------------------------
        // COLISÃO LATERAL / SUPERIOR
        // ---------------------------------------------

        killPlayer()

        return
    }
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
private fun isPlayerCollidingWithObject(
    levelObject: LevelObject
): Boolean {

    val definition =
        ObjectRegistry.get(
            levelObject.id
        )

    // =================================================
    // BLOCK — HITBOX NÃO ROTACIONADA
    // =================================================

    if (
        definition.type ==
        ObjectType.BLOCK
    ) {

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

        val playerLeft =
            player.x

        val playerRight =
            player.x +
            player.width

        val playerTop =
            player.y

        val playerBottom =
            player.y +
            player.height

        return (
            playerRight > objectLeft &&
            playerLeft < objectRight &&
            playerBottom > objectTop &&
            playerTop < objectBottom
        )
    }

    // =================================================
    // OUTROS OBJETOS — SAT ROTACIONADO
    // =================================================

    val objectWidth =
        if (
            definition.type ==
            ObjectType.HAZARD
        ) {
            32f *
                levelObject.scaleX
        } else {
            64f *
                levelObject.scaleX
        }

    val objectHeight =
        if (
            definition.type ==
            ObjectType.HAZARD
        ) {
            40f *
                levelObject.scaleY
        } else {
            64f *
                levelObject.scaleY
        }

    val objectCenterX =
        levelObject.x +
        32f *
        levelObject.scaleX

    val objectCenterY =
        levelObject.y +
        32f *
        levelObject.scaleY

    val playerCenterX =
        player.x +
        player.width / 2f

    val playerCenterY =
        player.y +
        player.height / 2f

    val angle =
        Math.toRadians(
            levelObject.rotation.toDouble()
        )

    val cos =
        kotlin.math.cos(
            angle
        ).toFloat()

    val sin =
        kotlin.math.sin(
            angle
        ).toFloat()

    val axes =
        arrayOf(
            floatArrayOf(1f, 0f),
            floatArrayOf(0f, 1f),
            floatArrayOf(cos, sin),
            floatArrayOf(-sin, cos)
        )

    val dx =
        playerCenterX -
        objectCenterX

    val dy =
        playerCenterY -
        objectCenterY

    for (
        axis in axes
    ) {

        val axisX =
            axis[0]

        val axisY =
            axis[1]

        val playerRadius =
            player.width / 2f *
            kotlin.math.abs(axisX) +
            player.height / 2f *
            kotlin.math.abs(axisY)

        val objectRadius =
            objectWidth / 2f *
            kotlin.math.abs(
                axisX * cos +
                axisY * sin
            ) +
            objectHeight / 2f *
            kotlin.math.abs(
                axisX * -sin +
                axisY * cos
            )

        val distance =
            kotlin.math.abs(
                dx * axisX +
                dy * axisY
            )

        if (
            distance >
            playerRadius +
            objectRadius
        ) {
            return false
        }
    }

    return true
}

private fun getBlockSurfaceY(
    levelObject: LevelObject,
    playerX: Float,
    inverted: Boolean
): Float? {

    val width =
        64f * levelObject.scaleX

    val height =
        64f * levelObject.scaleY

    val centerX =
        levelObject.x +
        width / 2f

    val centerY =
        levelObject.y +
        height / 2f

    val angle =
        Math.toRadians(
            levelObject.rotation.toDouble()
        )

    val cos =
        kotlin.math.cos(angle).toFloat()

    val sin =
        kotlin.math.sin(angle).toFloat()

    val halfWidth =
        width / 2f

    val halfHeight =
        height / 2f

    /*
     * Face do bloco que o jogador deve tocar.
     *
     * Y-down:
     *
     * gravidade normal    -> face superior
     * gravidade invertida -> face inferior
     */
    val localY =
        if (inverted) {
            halfHeight
        } else {
            -halfHeight
        }

    val leftX =
        -halfWidth

    val rightX =
        halfWidth

    /*
     * Rotação no sistema Y-down.
     */
    val leftWorldX =
        centerX +
        leftX * cos +
        localY * sin

    val leftWorldY =
        centerY -
        leftX * sin +
        localY * cos

    val rightWorldX =
        centerX +
        rightX * cos +
        localY * sin

    val rightWorldY =
        centerY -
        rightX * sin +
        localY * cos

    val dx =
        rightWorldX -
        leftWorldX

    /*
     * Se a superfície estiver vertical,
     * não existe uma altura única para X.
     */
    if (
        kotlin.math.abs(dx) <
        0.001f
    ) {
        return null
    }

    val t =
        (
            playerX -
            leftWorldX
        ) / dx

    /*
     * O jogador precisa estar sobre a face.
     */
    if (
        t < 0f ||
        t > 1f
    ) {
        return null
    }

    return leftWorldY +
        (
            rightWorldY -
            leftWorldY
        ) * t
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

if (
    isPlayerCollidingWithObject(
        levelObject
    )
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
        musicManager.restart()
        musicManager.stop()
        musicManager.play(level.music)
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
                                levelObject.scaleY,
                            rotation = levelObject.rotation
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
                                levelObject.scaleY,
                            rotation = levelObject.rotation
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
                                levelObject.scaleY,
                            rotation = levelObject.rotation
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
private fun updateMusic() {
    musicManager.play(level.music)
}

init {
    updateMusic()
}

private fun isTouchingReverseTrigger(): Boolean {

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
            "trigger.reverse"
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

if (
    isPlayerCollidingWithObject(
        levelObject
    )
) {
            return true
        }
    }

    return false
}

private fun isTouchingInvertTrigger(): Boolean {

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
            "trigger.invert"
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

if (
    isPlayerCollidingWithObject(
        levelObject
    )
) {
            return true
        }
    }

    return false
}
}