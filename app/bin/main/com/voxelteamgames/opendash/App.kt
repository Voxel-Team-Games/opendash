package com.voxelteamgames.opendash

import com.voxelteamgames.opendash.game.Game
import com.voxelteamgames.opendash.engine.graphics.*

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main() {

    if (!glfwInit()) {
        error("Não foi possível inicializar o GLFW.")
    }

    glfwDefaultWindowHints()

    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    val window = glfwCreateWindow(
        1280,
        720,
        "OpenDash",
        0,
        0
    )

    if (window == 0L) {
        glfwTerminate()
        error("Não foi possível criar a janela.")
    }

    glfwMakeContextCurrent(window)

    GL.createCapabilities()

    glfwSwapInterval(1)

    glfwShowWindow(window)

    println("OpenDash iniciado!")
    println("LWJGL inicializado.")
    println("OpenGL: ${glGetString(GL_VERSION)}")
    println("Renderer: ${glGetString(GL_RENDERER)}")

    // ------------------------------------------------
    // TEXTURAS
    // ------------------------------------------------

    val textures = TextureManager()

    val sky = textures.load(
        "sky",
        "/textures/sky/raster_1.png"
    )

    val ground = textures.load(
        "ground",
        "/textures/ground/raster_1.png"
    )

    val block = textures.load(
        "block",
        "/textures/blocks/pixel_default/pixel_iron_block.png"
    )

    val playerTexture = textures.load(
        "player",
        "/textures/players/cube/0.png"
    )

    val spike = textures.load(
        "spike",
        "/textures/hazards/pixel_default/pixel_yellow_spike.png"
    )

    val squareStream =
    Thread.currentThread()
        .contextClassLoader
        .getResourceAsStream(
            "textures/deco/modern/circle.svg"
        )
        ?: error("svg não encontrado")

val square = SvgParser().parse(
    squareStream
)


    // ------------------------------------------------
    // RENDERER
    // ------------------------------------------------

    val renderer = SpriteRenderer()
    val camera = Camera(
    x = 640f,
    y = 360f
)
    val vectorRenderer = VectorRenderer()
    // ------------------------------------------------
    // GAME
    // ------------------------------------------------

    val game = Game(
        playerTexture
    )

    // ------------------------------------------------
    // GAME LOOP
    // ------------------------------------------------

    var lastTime = glfwGetTime()

var restartWasPressed = false

    while (!glfwWindowShouldClose(window)) {
    
        val currentTime = glfwGetTime()

        var deltaTime =
            (currentTime - lastTime).toFloat()

        lastTime = currentTime

        // Evita problemas caso o jogo congele
        // por alguns segundos.
        if (deltaTime > 0.1f) {
            deltaTime = 0.1f
        }

// -----------------------------
// UPDATE
// -----------------------------

val restartPressed =
    glfwGetKey(
        window,
        GLFW_KEY_R
    ) == GLFW_PRESS

if (restartPressed && !restartWasPressed) {
    game.restart()
}

restartWasPressed = restartPressed

game.update(
    deltaTime,
    window
)

camera.follow(
    game.player.x,
    game.player.y
)
// -----------------------------
// RENDER
// -----------------------------

        glClearColor(
            0f,
            0f,
            0f,
            1f
        )

        glClear(GL_COLOR_BUFFER_BIT)

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

val skySize = 720f

val visibleLeft =
    camera.x - camera.width / 2f

val visibleRight =
    camera.x + camera.width / 2f

val visibleTop =
    camera.y - camera.height / 2f

val visibleBottom =
    camera.y + camera.height / 2f

val skyStartX =
    kotlin.math.floor(visibleLeft / skySize) * skySize

val skyStartY =
    kotlin.math.floor(visibleTop / skySize) * skySize

val skyEndX =
    visibleRight + skySize

val skyEndY =
    visibleBottom + skySize

var skyX = skyStartX

while (skyX <= skyEndX) {

    var skyY = skyStartY

    while (skyY <= skyEndY) {

        renderer.draw(
            sky,
            skyX,
            skyY,
            skySize,
            skySize
        )

        skyY += skySize
    }

    skyX += skySize
}
val tileSize = 64f

val startX =
    kotlin.math.floor(visibleLeft / tileSize) * tileSize

val endX =
    visibleRight + tileSize

var x = startX

while (x <= endX) {

    renderer.draw(
        ground,
        x,
        640f,
        tileSize,
        tileSize
    )

    x += tileSize
}

        // Plataformas
        renderer.draw(
            block,
            350f,
            500f,
            64f,
            64f
        )

        renderer.draw(
            block,
            414f,
            500f,
            64f,
            64f
        )

        renderer.draw(
            block,
            478f,
            500f,
            64f,
            64f
        )

        // Spike
        renderer.draw(
            spike,
            700f,
            576f,
            64f,
            64f
        )

vectorRenderer.draw(
    shape = square,
    x = 800f,
    y = 400f,
    scaleX = 128f,
    scaleY = 64f,
    rotation = 25f,
    colorR = 1f,
    colorG = 0.2f,
    colorB = 0.8f,
    colorA = 1f
)

        // Player
        game.render(renderer)

        renderer.end()

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    // ------------------------------------------------
    // CLEANUP
    // ------------------------------------------------

    textures.destroy()

    glfwDestroyWindow(window)

    glfwTerminate()
}