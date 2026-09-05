package com.voxelteamgames.morphjump

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.InputAdapter

import com.voxelteamgames.morphjump.engine.graphics.Camera
import com.voxelteamgames.morphjump.engine.graphics.SpriteRenderer
import com.voxelteamgames.morphjump.engine.graphics.Texture
import com.voxelteamgames.morphjump.engine.graphics.TextureManager
import com.voxelteamgames.morphjump.engine.graphics.VectorRenderer
import com.voxelteamgames.morphjump.engine.graphics.VectorShape
import com.voxelteamgames.morphjump.engine.graphics.SvgParser
import com.voxelteamgames.morphjump.game.DefaultObjects
import com.voxelteamgames.morphjump.game.Game
import com.voxelteamgames.morphjump.game.LevelEditor
import com.voxelteamgames.morphjump.game.LevelStorage
import kotlin.math.floor

enum class AppMode {
    GAME,
    EDITOR
}

class morphjumpGame : ApplicationAdapter() {

    // =================================================
    // TEXTURAS
    // =================================================

private var autoSaveTimer = 0f

private var levelDirty = false

private val AUTO_SAVE_INTERVAL = 10f

    private val textures =
        mutableMapOf<String, Texture>()

    private lateinit var textureManager: TextureManager

    // =================================================
    // VETORES
    // =================================================

    private val vectors =
        mutableMapOf<String, VectorShape>()

    // =================================================
    // RENDERERS
    // =================================================

    private lateinit var renderer: SpriteRenderer
    private lateinit var vectorRenderer: VectorRenderer
    private var reverseTriggerUsed = false
    private lateinit var camera: Camera

    // =================================================
    // GAME
    // =================================================

    private lateinit var game: Game
    private lateinit var editor: LevelEditor

    // =================================================
    // LEVEL
    // =================================================

    private lateinit var level: com.voxelteamgames.morphjump.game.Level

    private var currentLevelNumber = 1

    private var currentLevelName =
        "level_$currentLevelNumber.json"

    // =================================================
    // MODO
    // =================================================

    private var mode =
        AppMode.GAME

    // =================================================
    // INPUT STATE
    // =================================================

    private var restartWasPressed = false
    private var editorToggleWasPressed = false
    private var saveWasPressed = false
    private var saveAsWasPressed = false
    private var loadWasPressed = false

private val inputProcessor =
    object : InputAdapter() {

        override fun scrolled(
            amountX: Float,
            amountY: Float
        ): Boolean {

            if (::editor.isInitialized) {
                editor.onMouseScrolled(
                    amountY.toInt()
                )
            }

            return false
        }
    }

    // =================================================
    // CREATE
    // =================================================

    override fun create() {

        println("morphjump iniciado com LibGDX!")

        println(
            "Renderer: ${Gdx.gl.glGetString(GL20.GL_RENDERER)}"
        )

        println(
            "OpenGL: ${Gdx.gl.glGetString(GL20.GL_VERSION)}"
        )

        // =================================================
        // TEXTURAS
        // =================================================

        textureManager =
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
            "/textures/deco/yellow_orb.png"
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
            "/textures/deco/cube_portal.png"
        )

        loadTexture(
            "/textures/deco/ship_portal.png"
        )

        loadTexture(
            "/textures/deco/finish.png"
        )

        loadTexture(
            "/textures/players/cube/0.png"
        )

        loadTexture(
            "/textures/trigger/invert_trigger.png"
        )

        loadTexture(
            "/textures/trigger/reverse_trigger.png"
        )
        loadTexture(
            "/textures/deco/raster_yellow_spike_back.png"
        )
        loadTexture(
            "/textures/deco/raster_iron_block_back.png"
        )
        loadTexture(
            "/textures/deco/jump_marker.png"
        )
        loadTexture(
            "/textures/deco/jump_marker_2.png"
        )
        loadTexture(
            "/textures/deco/white_square.png"
        )
        loadTexture(
            "/textures/deco/gray_square.png"
        )
        loadTexture(
            "/textures/deco/black_square.png"
        )


        // =================================================
        // VETORES
        // =================================================

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

        renderer =
            SpriteRenderer()

        vectorRenderer =
            VectorRenderer()

        camera =
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

        level =
            LevelStorage.load(
                currentLevelName
            )

        println(
            "Fase inicial carregada: $currentLevelName"
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

        game =
            Game(
                playerTexture,
                level
            )

        // =================================================
        // EDITOR
        // =================================================

        editor =
            LevelEditor(
                level
            )
        
        Gdx.input.inputProcessor =
            inputProcessor

        camera.follow(
            game.playerX,
            game.playerY
        )
    }

    // =================================================
    // LOAD LEVEL
    // =================================================

    private fun loadLevel(
        levelNumber: Int
    ) {

        val newLevelName =
            "level_$levelNumber.json"

        println(
            "Carregando fase: $newLevelName"
        )

        val loadedLevel =
            try {

                LevelStorage.load(
                    newLevelName
                )

            } catch (exception: Exception) {

                println(
                    "Não foi possível carregar $newLevelName"
                )

                println(
                    "Erro: ${exception.message}"
                )

                return
            }

level.objects.clear()

level.objects.addAll(
    loadedLevel.objects
)

level.music =
    loadedLevel.music

currentLevelNumber =
    levelNumber
    
        currentLevelName =
            newLevelName

        game.restart()

        camera.follow(
            game.playerX,
            game.playerY
        )

        println(
            "Fase carregada com sucesso: $currentLevelName"
        )
    }

    // =================================================
    // UPDATE
    // =================================================

    private fun update(
        deltaTime: Float
    ) {
        // =================================================
        // MODIFICADORES
        // =================================================

        val ctrlPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.CONTROL_LEFT
            ) ||
            Gdx.input.isKeyPressed(
                Input.Keys.CONTROL_RIGHT
            )

        val shiftPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.SHIFT_LEFT
            ) ||
            Gdx.input.isKeyPressed(
                Input.Keys.SHIFT_RIGHT
            )

        // =================================================
        // CTRL + P
        // =================================================

        val pPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.P
            )

        val editorTogglePressed =
            ctrlPressed &&
            pPressed

        if (
            editorTogglePressed &&
            !editorToggleWasPressed
        ) {

            mode =
                if (
                    mode == AppMode.GAME
                ) {
                    AppMode.EDITOR
                } else {
                    AppMode.GAME
                }

            println(
                if (
                    mode == AppMode.EDITOR
                ) {
                    "Modo editor ativado!"
                } else {
                    "Modo jogo ativado!"
                }
            )
        }

        editorToggleWasPressed =
            editorTogglePressed

        // =================================================
        // CTRL + S
        // =================================================

        val sPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.S
            )

        val saveAsPressed =
            ctrlPressed &&
            shiftPressed &&
            sPressed

        val savePressed =
            ctrlPressed &&
            sPressed &&
            !shiftPressed

        // =================================================
        // SALVAR COMO
        // =================================================

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
                "Fase salva como: $currentLevelName"
            )
        }

        saveAsWasPressed =
            saveAsPressed

if (mode == AppMode.EDITOR) {

    autoSaveTimer += deltaTime

    if (autoSaveTimer >= AUTO_SAVE_INTERVAL) {

        LevelStorage.save(
            level,
            currentLevelName
        )

        autoSaveTimer = 0f

        println(
            "Autosave: $currentLevelName"
        )
    }
}

        // =================================================
        // SALVAR COMO
        // =================================================

        if (
            saveAsPressed &&
            !saveAsWasPressed &&
            mode == AppMode.EDITOR
        ) {

            val newLevelNumber =
                currentLevelNumber + 1

            val newName =
                "level_$newLevelNumber.json"

            LevelStorage.save(
                level,
                newName
            )

            currentLevelNumber =
                newLevelNumber

            currentLevelName =
                newName

            println(
                "Fase salva como: $currentLevelName"
            )
        }

        saveAsWasPressed =
            saveAsPressed

        // =================================================
        // CTRL + O
        // =================================================

        val oPressed =
            Gdx.input.isKeyPressed(
                Input.Keys.O
            )

        val loadPressed =
            ctrlPressed &&
            oPressed

        if (
            loadPressed &&
            !loadWasPressed &&
            mode == AppMode.EDITOR
        ) {

            val loadedLevel =
                try {

                    LevelStorage.load(
                        currentLevelName
                    )

                } catch (exception: Exception) {

                    println(
                        "Erro ao carregar $currentLevelName: " +
                        exception.message
                    )

                    null
                }

            if (
                loadedLevel != null
            ) {

                level.objects.clear()

                level.objects.addAll(
                    loadedLevel.objects
                )

                println(
                    "Fase carregada: $currentLevelName"
                )
            }
        }

        loadWasPressed =
            loadPressed

        // =================================================
        // RESTART
        // =================================================

        if (
            mode == AppMode.GAME
        ) {

            val restartPressed =
                Gdx.input.isKeyPressed(
                    Input.Keys.R
                )

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

        // =================================================
        // GAME / EDITOR
        // =================================================

        when (mode) {

            AppMode.GAME -> {

                /*
                 * IMPORTANTE:
                 *
                 * Por enquanto o Game ainda usa GLFW.
                 * Vamos remover isso no próximo passo.
                 */

                game.update(
                    deltaTime,
                    0L
                )

                if (
                    game.nextLevelRequested
                ) {

                    val nextLevel =
                        currentLevelNumber + 1

                    println(
                        "Trigger de fim ativado!"
                    )

                    println(
                        "Tentando carregar fase $nextLevel..."
                    )

                    loadLevel(
                        nextLevel
                    )

                    game.clearNextLevelRequest()
                }

                camera.follow(
                    game.playerX,
                    game.playerY
                )
            }

            AppMode.EDITOR -> {

                /*
                 * O editor também ainda usa a API antiga.
                 *
                 * Vamos migrá-lo depois do Game.
                 */

                editor.update(
                    camera,
                    deltaTime
                )
            }
        }
    }

    // =================================================
    // RENDER
    // =================================================

    override fun render() {

        val deltaTime =
            Gdx.graphics.deltaTime.coerceAtMost(
                0.1f
            )

        update(
            deltaTime
        )

        // =================================================
        // CLEAR
        // =================================================

        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        )

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        )

        // =================================================
        // BEGIN
        // =================================================

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

        // =================================================
        // SKY
        // =================================================

        val sky =
            textures[
                "/textures/sky/raster_1.png"
            ]
                ?: error(
                    "Sky não carregado."
                )

        val skySize =
            1620f

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
            skyStartX -
            camera.x * 0.1f

        while (
            skyX <= skyEndX
        ) {

            var skyY =
                skyStartY -
                720f

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

        // =================================================
        // GROUND
        // =================================================

        val ground =
            textures[
                "/textures/ground/raster_1.png"
            ]
                ?: error(
                    "Ground não carregado."
                )

        val tileSize =
            320f

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

        // =================================================
        // GAME / EDITOR
        // =================================================

        when (mode) {

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

        // =================================================
        // EDITOR
        // =================================================

        if (
            mode == AppMode.EDITOR
        ) {

            editor.renderGrid(
                vectorRenderer,
                camera
            )
        }

        // =================================================
        // END
        // =================================================

        vectorRenderer.end()
        renderer.end()
    }

    // =================================================
    // RESIZE
    // =================================================

    override fun resize(
        width: Int,
        height: Int
    ) {

        println(
            "morphjump redimensionado: ${width}x$height"
        )
    }

    // =================================================
    // DISPOSE
    // =================================================

    override fun dispose() {

        textureManager.destroy()
    }
}