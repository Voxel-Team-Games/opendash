package com.voxelteamgames.opendash

import com.voxelteamgames.opendash.engine.graphics.Camera
import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import com.voxelteamgames.opendash.engine.graphics.TextureManager
import com.voxelteamgames.opendash.engine.graphics.VectorRenderer
import com.voxelteamgames.opendash.engine.graphics.VectorShape
import com.voxelteamgames.opendash.engine.graphics.SvgParser

import com.voxelteamgames.opendash.game.DefaultObjects
import com.voxelteamgames.opendash.game.Game
import com.voxelteamgames.opendash.game.LevelEditor
import com.voxelteamgames.opendash.game.LevelStorage

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

import kotlin.math.floor

enum class AppMode {
    GAME,
    EDITOR
}

fun main() {

    // =================================================
    // GLFW
    // =================================================

    if (!glfwInit()) {
        error(
            "Não foi possível inicializar o GLFW."
        )
    }

    glfwDefaultWindowHints()

    glfwWindowHint(
        GLFW_VISIBLE,
        GLFW_FALSE
    )

    glfwWindowHint(
        GLFW_RESIZABLE,
        GLFW_TRUE
    )

    val window =
        glfwCreateWindow(
            1280,
            720,
            "OpenDash",
            0,
            0
        )

    if (window == 0L) {

        glfwTerminate()

        error(
            "Não foi possível criar a janela."
        )
    }

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    glfwSwapInterval(1)

    glfwShowWindow(window)

    println("OpenDash iniciado!")
    println("LWJGL inicializado.")

    println(
        "OpenGL: ${glGetString(GL_VERSION)}"
    )

    println(
        "Renderer: ${glGetString(GL_RENDERER)}"
    )

    // =================================================
    // TEXTURAS
    // =================================================

    val textures =
        mutableMapOf<String, Texture>()

    val textureManager =
        TextureManager()

    fun loadTexture(
        path: String
    ) {

        textures[path] =
            textureManager.load(
                path,
                path
            )
    }

    loadTexture(
        "/textures/sky/raster_1.png"
    )

    loadTexture(
        "/textures/ground/raster_1.png"
    )

    loadTexture(
        "/textures/blocks/default/raster_iron_block.png"
    )

    loadTexture(
        "/textures/hazards/default/raster_yellow_spike.png"
    )

    loadTexture(
        "/textures/players/cube/0.png"
    )

    // =================================================
    // VETORES
    // =================================================

    val vectors =
        mutableMapOf<String, VectorShape>()

    fun loadVector(
        path: String
    ) {

        val stream =
            Thread.currentThread()
                .contextClassLoader
                .getResourceAsStream(
                    path.removePrefix("/")
                )
                ?: error(
                    "SVG não encontrado: $path"
                )

        stream.use {

            vectors[path] =
                SvgParser().parse(it)
        }
    }

    loadVector(
        "/textures/deco/modern/circle.svg"
    )

    loadVector(
        "/textures/deco/modern/square.svg"
    )

    loadVector(
        "/textures/hazards/modern/solid_spike.svg"
    )

    loadVector(
        "/textures/blocks/default/iron_block.svg"
    )

    loadVector(
        "/textures/hazards/default/yellow_spike.svg"
    )

    // =================================================
    // RENDERERS
    // =================================================

    val renderer =
        SpriteRenderer()

    val vectorRenderer =
        VectorRenderer()

    val camera =
        Camera(
            x = 640f,
            y = 360f
        )

    // =================================================
    // OBJETOS
    // =================================================

    DefaultObjects.register()

    // =================================================
    // LEVEL
    // =================================================

    var currentLevelName =
        "level_001.json"

    val level =
        LevelStorage.load(
            currentLevelName
        )

    // =================================================
    // PLAYER
    // =================================================

    val playerTexture =
        textures[
            "/textures/players/cube/0.png"
        ]
            ?: error(
                "Textura do player não carregada."
            )

    // =================================================
    // GAME
    // =================================================

    val game =
        Game(
            playerTexture,
            level
        )

    // =================================================
    // EDITOR
    // =================================================

    val editor =
        LevelEditor(
            level
        )

    var mode =
        AppMode.GAME

    // =================================================
    // INPUT STATE
    // =================================================

    var lastTime =
        glfwGetTime()

    var restartWasPressed =
        false

    var editorToggleWasPressed =
        false

    var saveWasPressed =
        false

    var saveAsWasPressed =
        false

    var loadWasPressed =
        false

    // =================================================
    // GAME LOOP
    // =================================================

    while (
        !glfwWindowShouldClose(window)
    ) {

        // ---------------------------------------------
        // DELTA TIME
        // ---------------------------------------------

        val currentTime =
            glfwGetTime()

        var deltaTime =
            (
                currentTime -
                lastTime
            ).toFloat()

        lastTime =
            currentTime

        if (
            deltaTime > 0.1f
        ) {
            deltaTime = 0.1f
        }

        // ---------------------------------------------
        // MODIFICADORES
        // ---------------------------------------------

        val ctrlPressed =
            glfwGetKey(
                window,
                GLFW_KEY_LEFT_CONTROL
            ) == GLFW_PRESS ||
            glfwGetKey(
                window,
                GLFW_KEY_RIGHT_CONTROL
            ) == GLFW_PRESS

        val shiftPressed =
            glfwGetKey(
                window,
                GLFW_KEY_LEFT_SHIFT
            ) == GLFW_PRESS ||
            glfwGetKey(
                window,
                GLFW_KEY_RIGHT_SHIFT
            ) == GLFW_PRESS

        // ---------------------------------------------
        // TECLAS
        // ---------------------------------------------

        val pPressed =
            glfwGetKey(
                window,
                GLFW_KEY_P
            ) == GLFW_PRESS

        val sPressed =
            glfwGetKey(
                window,
                GLFW_KEY_S
            ) == GLFW_PRESS

        val oPressed =
            glfwGetKey(
                window,
                GLFW_KEY_O
            ) == GLFW_PRESS

        // ---------------------------------------------
        // CTRL + P
        // ---------------------------------------------

        val editorTogglePressed =
            ctrlPressed &&
            pPressed

        if (
            editorTogglePressed &&
            !editorToggleWasPressed
        ) {

            mode =
                if (
                    mode ==
                    AppMode.GAME
                ) {
                    AppMode.EDITOR
                } else {
                    AppMode.GAME
                }

            println(
                if (
                    mode ==
                    AppMode.EDITOR
                ) {
                    "Modo editor ativado!"
                } else {
                    "Modo jogo ativado!"
                }
            )
        }

        editorToggleWasPressed =
            editorTogglePressed

        // ---------------------------------------------
        // CTRL + S
        // ---------------------------------------------

        val saveAsPressed =
            ctrlPressed &&
            shiftPressed &&
            sPressed

        val savePressed =
            ctrlPressed &&
            sPressed &&
            !shiftPressed

        // ---------------------------------------------
        // SALVAR
        // ---------------------------------------------

        if (
            savePressed &&
            !saveWasPressed &&
            mode == AppMode.EDITOR
        ) {

            LevelStorage.save(
                level,
                currentLevelName
            )

            println(
                "Fase salva: $currentLevelName"
            )
        }

        saveWasPressed =
            savePressed

        // ---------------------------------------------
        // SALVAR COMO
        // ---------------------------------------------

        if (
            saveAsPressed &&
            !saveAsWasPressed &&
            mode == AppMode.EDITOR
        ) {

            val newName =
                "level_${System.currentTimeMillis()}.json"

            LevelStorage.save(
                level,
                newName
            )

            currentLevelName =
                newName

            println(
                "Fase salva como: $currentLevelName"
            )
        }

        saveAsWasPressed =
            saveAsPressed

        // ---------------------------------------------
        // CARREGAR
        // ---------------------------------------------

        val loadPressed =
            ctrlPressed &&
            oPressed

        if (
            loadPressed &&
            !loadWasPressed &&
            mode == AppMode.EDITOR
        ) {

            val loadedLevel =
                LevelStorage.load(
                    currentLevelName
                )

            level.objects.clear()

            level.objects.addAll(
                loadedLevel.objects
            )

            println(
                "Fase carregada: $currentLevelName"
            )
        }

        loadWasPressed =
            loadPressed

        // ---------------------------------------------
        // RESTART
        // ---------------------------------------------

        if (
            mode ==
            AppMode.GAME
        ) {

            val restartPressed =
                glfwGetKey(
                    window,
                    GLFW_KEY_R
                ) == GLFW_PRESS

            if (
                restartPressed &&
                !restartWasPressed
            ) {

                game.restart()
            }

            restartWasPressed =
                restartPressed

        } else {

            restartWasPressed =
                false
        }

        // ---------------------------------------------
        // UPDATE
        // ---------------------------------------------

        when (
            mode
        ) {

            AppMode.GAME -> {

                game.update(
                    deltaTime,
                    window
                )

                camera.follow(
                    game.playerX,
                    game.playerY
                )
            }

            AppMode.EDITOR -> {

                editor.update(
                    window,
                    camera,
                    deltaTime
                )
            }
        }

        // ---------------------------------------------
        // CLEAR
        // ---------------------------------------------

        glClearColor(
            0f,
            0f,
            0f,
            1f
        )

        glClear(
            GL_COLOR_BUFFER_BIT
        )

        // ---------------------------------------------
        // BEGIN
        // ---------------------------------------------

        renderer.begin(
            1280,
            720,
            camera
        )

        vectorRenderer.begin(
            1280,
            720,
            camera
        )

        // ---------------------------------------------
        // SKY
        // ---------------------------------------------

        val sky =
            textures[
                "/textures/sky/raster_1.png"
            ]
                ?: error(
                    "Sky não carregado."
                )

        val skySize =
            720f

        val visibleLeft =
            camera.x -
            camera.width / 2f

        val visibleRight =
            camera.x +
            camera.width / 2f

        val visibleTop =
            camera.y -
            camera.height / 2f

        val visibleBottom =
            camera.y +
            camera.height / 2f

        val skyStartX =
            floor(
                visibleLeft /
                    skySize
            ) * skySize

        val skyStartY =
            floor(
                visibleTop /
                    skySize
            ) * skySize

        val skyEndX =
            visibleRight +
            skySize

        val skyEndY =
            visibleBottom +
            skySize

        var skyX =
            skyStartX

        while (
            skyX <= skyEndX
        ) {

            var skyY =
                skyStartY

            while (
                skyY <= skyEndY
            ) {

                renderer.draw(
                    sky,
                    skyX,
                    skyY,
                    skySize,
                    skySize
                )

                skyY +=
                    skySize
            }

            skyX +=
                skySize
        }

        // ---------------------------------------------
        // GROUND
        // ---------------------------------------------

        val ground =
            textures[
                "/textures/ground/raster_1.png"
            ]
                ?: error(
                    "Ground não carregado."
                )

        val tileSize =
            64f

        val startX =
            floor(
                visibleLeft /
                    tileSize
            ) * tileSize

        val endX =
            visibleRight +
            tileSize

        var groundX =
            startX

        while (
            groundX <= endX
        ) {

            renderer.draw(
                ground,
                groundX,
                640f,
                tileSize,
                tileSize
            )

            groundX +=
                tileSize
        }

        // ---------------------------------------------
        // LEVEL / PLAYER
        // ---------------------------------------------

        when (
            mode
        ) {

            AppMode.GAME -> {

                game.render(
                    renderer,
                    vectorRenderer,
                    textures,
                    vectors
                )
            }

            AppMode.EDITOR -> {

                editor.render(
                    renderer,
                    vectorRenderer,
                    textures,
                    vectors
                )
            }
        }

        // ---------------------------------------------
        // EDITOR GRID
        // ---------------------------------------------

        if (
            mode ==
            AppMode.EDITOR
        ) {

            editor.renderGrid(
                vectorRenderer,
                camera
            )

            editor.renderCursor(
                vectorRenderer
            )

            editor.renderSelection(
                vectorRenderer
            )
        }

        // ---------------------------------------------
        // END
        // ---------------------------------------------

        vectorRenderer.end()
        renderer.end()

        // ---------------------------------------------
        // DISPLAY
        // ---------------------------------------------

        glfwSwapBuffers(
            window
        )

        glfwPollEvents()
    }

    // =================================================
    // CLEANUP
    // =================================================

    textureManager.destroy()

    glfwDestroyWindow(
        window
    )

    glfwTerminate()
}