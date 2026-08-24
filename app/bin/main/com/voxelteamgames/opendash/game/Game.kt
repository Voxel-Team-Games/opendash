package com.voxelteamgames.opendash.game

import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import org.lwjgl.glfw.GLFW.*

data class Platform(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

class Game(
    private val playerTexture: Texture
) {

    private val player = Player(
        x = 100f,
        y = 576f
    )

    private var jumpWasPressed = false

    private var deathTimer = 0f

    companion object {
        const val RESPAWN_DELAY = 1f

        const val SPAWN_X = 100f
        const val SPAWN_Y = 576f
    }

    // -----------------------------------------
    // PLATAFORMAS
    // -----------------------------------------

    private val platforms = listOf(

        // Chão
        Platform(
            0f,
            640f,
            1280f,
            64f
        ),

        // Plataforma
        Platform(
            350f,
            500f,
            192f,
            64f
        ),

        // Parede
        Platform(
            900f,
            400f,
            64f,
            240f
        )
    )

    // -----------------------------------------
    // HAZARDS
    // -----------------------------------------

    private val hazards = listOf(

        Platform(
            700f,
            576f,
            64f,
            64f
        )
    )

    fun update(
        deltaTime: Float,
        window: Long
    ) {

        // -----------------------------------------
        // MORTE / RESPAWN
        // -----------------------------------------

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

        // -----------------------------------------
        // INPUT
        // -----------------------------------------

        val jumpPressed =
            glfwGetKey(
                window,
                GLFW_KEY_SPACE
            ) == GLFW_PRESS ||

            glfwGetMouseButton(
                window,
                GLFW_MOUSE_BUTTON_LEFT
            ) == GLFW_PRESS

        if (jumpPressed && !jumpWasPressed) {
            player.jump()
        }

        jumpWasPressed = jumpPressed

        // -----------------------------------------
        // GUARDA POSIÇÃO ANTERIOR
        // -----------------------------------------

        val previousX = player.x

        val previousBottom =
            player.y + player.height

        // -----------------------------------------
        // FÍSICA
        // -----------------------------------------

        player.update(deltaTime)

        val currentBottom =
            player.y + player.height

        player.grounded = false

        // -----------------------------------------
        // COLISÃO COM PLATAFORMAS
        // -----------------------------------------

        for (platform in platforms) {

            val playerRight =
                player.x + player.width

            val platformRight =
                platform.x + platform.width

            val horizontalCollision =
                playerRight > platform.x &&
                player.x < platformRight

            val crossedPlatform =
                previousBottom <= platform.y &&
                currentBottom >= platform.y

            if (
                horizontalCollision &&
                crossedPlatform &&
                player.velocityY >= 0f
            ) {

                player.landOn(platform.y)

                break
            }
        }

        // -----------------------------------------
        // COLISÃO LATERAL COM PAREDES
        // -----------------------------------------

        for (wall in platforms) {

            val verticalCollision =
                player.y + player.height > wall.y &&
                player.y < wall.y + wall.height

            val crossedLeftSide =
                previousX + player.width <= wall.x &&
                player.x + player.width >= wall.x

            if (
                verticalCollision &&
                crossedLeftSide
            ) {

                player.kill()
                deathTimer = 0f

                return
            }
        }

        // -----------------------------------------
        // COLISÃO COM HAZARDS
        // -----------------------------------------

        for (hazard in hazards) {

            val horizontalCollision =
                player.x + player.width > hazard.x &&
                player.x < hazard.x + hazard.width

            val verticalCollision =
                player.y + player.height > hazard.y &&
                player.y < hazard.y + hazard.height

            if (
                horizontalCollision &&
                verticalCollision
            ) {

                player.kill()
                deathTimer = 0f

                return
            }
        }

        // -----------------------------------------
        // CAIU DA FASE
        // -----------------------------------------

        if (player.y > 800f) {

            player.kill()
            deathTimer = 0f
        }
    }

    fun restart() {

        player.reset(
            SPAWN_X,
            SPAWN_Y
        )

        deathTimer = 0f
        jumpWasPressed = false
    }

    fun render(renderer: SpriteRenderer) {

        if (player.dead) {
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
}