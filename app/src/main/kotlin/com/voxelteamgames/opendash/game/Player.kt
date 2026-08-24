package com.voxelteamgames.opendash.game

class Player(
    var x: Float,
    var y: Float,
    val width: Float = 64f,
    val height: Float = 64f
) {

    var velocityY = 0f

    var grounded = false

    var dead = false

    companion object {
        const val AUTO_SPEED = 300f
        const val GRAVITY = 1800f
        const val JUMP_FORCE = 750f
    }

    fun update(deltaTime: Float) {

        // Movimento automático
        x += AUTO_SPEED * deltaTime

        // Gravidade
        velocityY += GRAVITY * deltaTime

        y += velocityY * deltaTime
    }

    fun jump() {

        if (dead) {
            return
        }

        if (!grounded) {
            return
        }

        velocityY = -JUMP_FORCE
        grounded = false
    }

    fun landOn(platformY: Float) {

        y = platformY - height

        velocityY = 0f

        grounded = true
    }

    fun kill() {

        if (dead) {
            return
        }

        dead = true
        velocityY = 0f
    }

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