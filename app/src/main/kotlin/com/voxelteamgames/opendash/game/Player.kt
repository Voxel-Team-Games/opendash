package com.voxelteamgames.opendash.game
import com.voxelteamgames.opendash.engine.audio.MusicManager

class Player(
    var x: Float,
    var y: Float,
    val width: Float = 64f,
    val height: Float = 64f
) {
        val musicManager =
    MusicManager()
    var velocityY = 0f

    var grounded = false

    var dead = false

    var gamemode =
        PlayerGamemode.CUBE

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

        const val SHIP_GRAVITY = 900f
        const val SHIP_THRUST = 1800f
        const val SHIP_MAX_SPEED_Y = 900f
    }

    // =================================================
    // UPDATE
    // =================================================

    fun update(
        deltaTime: Float,
        inputPressed: Boolean
    ) {

        if (dead) {
            return
        }

        // Movimento horizontal automático
        x += AUTO_SPEED * deltaTime

        when (gamemode) {

            // =================================================
            // CUBE
            // =================================================

            PlayerGamemode.CUBE -> {

                velocityY +=
                    CUBE_GRAVITY *
                    deltaTime

                y +=
                    velocityY *
                    deltaTime
            }

            // =================================================
            // SHIP
            // =================================================

            PlayerGamemode.SHIP -> {

                // Gravidade
                velocityY +=
                    SHIP_GRAVITY *
                    deltaTime

                // Propulsão
                if (inputPressed) {

                    velocityY -=
                        SHIP_THRUST *
                        deltaTime
                }

                // Limitar velocidade
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
                    deltaTime
            }
        }
    }

    // =================================================
    // CUBE - PULO
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

        velocityY =
            -CUBE_JUMP_FORCE

        grounded = false
    }

    // =================================================
    // CUBE - POUSO
    // =================================================

    fun landOn(
        platformY: Float
    ) {

        y =
            platformY -
            height

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

        grounded = false
               
        dead = false
    }
}