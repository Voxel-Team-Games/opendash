package com.voxelteamgames.morphjump.game

class Player(
    var x: Float,
    var y: Float,
    val width: Float = 64f,
    val height: Float = 64f
) {

    var velocityY = 0f

    var grounded = false

    var dead = false
var gravityInverted = false
    var gamemode =
        PlayerGamemode.CUBE

var moveDirection = 1f

    companion object {

        // =================================================
        // MOVIMENTO
        // =================================================

        const val AUTO_SPEED = 350f

        // =================================================
        // CUBE
        // =================================================

        const val CUBE_GRAVITY = 1800f
        const val CUBE_JUMP_FORCE = 800f

        // =================================================
        // SHIP
        // =================================================

        const val SHIP_GRAVITY = 800f
        const val SHIP_THRUST = 1800f
        const val SHIP_MAX_SPEED_Y = 900f
    }

    // =================================================
    // UPDATE
    // =================================================

fun update(
    deltaTime: Float,
    inputPressed: Boolean,
    horizontalSpeed: Float = AUTO_SPEED
){

    if (dead) {
        return
    }

    // =================================================
    // VELOCIDADE GLOBAL DO JOGO
    // =================================================

    val scaledDeltaTime =
        deltaTime * 1.3333f

    // =================================================
    // MOVIMENTO HORIZONTAL
    // =================================================

x +=
    horizontalSpeed *
    moveDirection *
    scaledDeltaTime

    when (gamemode) {

        // =================================================
        // CUBE
        // =================================================

        PlayerGamemode.CUBE -> {

            val gravity =
                if (gravityInverted) {
                    -CUBE_GRAVITY
                } else {
                    CUBE_GRAVITY
                }

            velocityY +=
                gravity *
                scaledDeltaTime

            y +=
                velocityY *
                scaledDeltaTime
        }

        // =================================================
        // SHIP
        // =================================================

        PlayerGamemode.SHIP -> {

            velocityY +=
                SHIP_GRAVITY *
                scaledDeltaTime

            if (inputPressed) {

                velocityY -=
                    SHIP_THRUST *
                    scaledDeltaTime
            }

            if (
                velocityY >
                SHIP_MAX_SPEED_Y
            ) {

                velocityY =
                    SHIP_MAX_SPEED_Y
            }

            if (
                velocityY <
                -SHIP_MAX_SPEED_Y
            ) {

                velocityY =
                    -SHIP_MAX_SPEED_Y
            }

            y +=
                velocityY *
                scaledDeltaTime
        }
    }
}

    // =================================================
    // CUBE - PULO NORMAL
    // =================================================

    fun jump() {

        if (dead) {
            return
        }

        if (
            gamemode !=
            PlayerGamemode.CUBE
        ) {
            return
        }

        if (!grounded) {
            return
        }

        performJump()
    }

    // =================================================
    // CUBE - PULO FORÇADO
    // =================================================

    /*
     * Usado por objetos como o Yellow Orb.
     *
     * Diferentemente de jump(), este método não exige
     * que o jogador esteja no chão.
     *
     * A decisão de quando ele pode ser usado pertence
     * ao Game.
     */
    fun forceJump() {

        if (dead) {
            return
        }

        if (
            gamemode !=
            PlayerGamemode.CUBE
        ) {
            return
        }

        performJump()
    }

    // =================================================
    // EXECUTAR PULO
    // =================================================

private fun performJump() {

    velocityY =
        if (gravityInverted) {
            CUBE_JUMP_FORCE
        } else {
            -CUBE_JUMP_FORCE
        }

    grounded = false
}

// =================================================
// CUBE - POUSO
// =================================================

fun landOn(
    surfaceY: Float
) {

    if (gravityInverted) {

        // Player fica abaixo da superfície
        y =
            surfaceY

    } else {

        // Player fica acima da superfície
        y =
            surfaceY -
            height
    }

    velocityY = 0f

    grounded = true
}

    // =================================================
    // MORTE
    // =================================================

    fun kill() {

        if (dead) {
            return
        }

        dead = true

        velocityY = 0f
    }


    // =================================================
    // RESET
    // =================================================

    fun reset(
        startX: Float,
        startY: Float
    ) {

        x = startX
        y = startY

        velocityY = 0f
        moveDirection = 1f
        gravityInverted = false
        grounded = false

        dead = false
    }
    fun reverseDirection() {

    moveDirection *= -1f
}
}