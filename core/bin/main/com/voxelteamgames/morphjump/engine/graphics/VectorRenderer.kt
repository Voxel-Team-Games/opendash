package com.voxelteamgames.morphjump.engine.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4

class VectorRenderer {

    private val shapeRenderer =
        ShapeRenderer()

    private var camera: Camera? = null

    private var screenWidth = 0
    private var screenHeight = 0

    fun begin(
    width: Int,
    height: Int,
    camera: Camera? = null
) {

    screenWidth = width
    screenHeight = height

    this.camera = camera

    val projection =
        Matrix4().setToOrtho2D(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )

    shapeRenderer.projectionMatrix =
        projection

    shapeRenderer.begin(
        ShapeRenderer.ShapeType.Filled
    )
}

    fun draw(
        shape: VectorShape,
        x: Float,
        y: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float
    ) {

        val currentCamera =
            camera

        val screenX: Float
        val screenY: Float
        val finalScaleX: Float
        val finalScaleY: Float

        if (currentCamera != null) {

            screenX =
                (x - currentCamera.x) *
                    currentCamera.zoom +
                    screenWidth / 2f

            screenY =
                (y - currentCamera.y) *
                    currentCamera.zoom +
                    screenHeight / 2f

            finalScaleX =
                scaleX * currentCamera.zoom

            finalScaleY =
                scaleY * currentCamera.zoom

        } else {

            screenX = x
            screenY = y

            finalScaleX = scaleX
            finalScaleY = scaleY
        }

        shapeRenderer.color =
            Color(
                colorR,
                colorG,
                colorB,
                colorA
            )

        val vertices =
            shape.vertices

        if (vertices.size < 6) {
            return
        }

        val transformed =
            FloatArray(vertices.size)

        val radians =
            Math.toRadians(
                rotation.toDouble()
            )

        val cos =
            kotlin.math.cos(radians).toFloat()

        val sin =
            kotlin.math.sin(radians).toFloat()

        var i = 0

        while (i < vertices.size) {

            val localX =
                vertices[i] *
                    finalScaleX

            val localY =
                vertices[i + 1] *
                    finalScaleY

            transformed[i] =
                screenX +
                    localX * cos -
                    localY * sin

            transformed[i + 1] =
                screenY +
                    localX * sin +
                    localY * cos

            i += 2
        }

        val firstX =
            transformed[0]

        val firstY =
            transformed[1]

        i = 2

        while (i < transformed.size - 2) {

            shapeRenderer.triangle(
                firstX,
                firstY,
                transformed[i],
                transformed[i + 1],
                transformed[i + 2],
                transformed[i + 3]
            )

            i += 2
        }
    }

    // =================================================
    // RECTÂNGULO
    // =================================================

    fun drawRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        colorR: Float,
        colorG: Float,
        colorB: Float,
        colorA: Float
    ) {

        val currentCamera =
            camera

        val screenX: Float
        val screenY: Float
        val finalWidth: Float
        val finalHeight: Float

        if (currentCamera != null) {

            screenX =
                (x - currentCamera.x) *
                    currentCamera.zoom +
                    screenWidth / 2f

            screenY =
                (y - currentCamera.y) *
                    currentCamera.zoom +
                    screenHeight / 2f

            finalWidth =
                width * currentCamera.zoom

            finalHeight =
                height * currentCamera.zoom

        } else {

            screenX = x
            screenY = y

            finalWidth = width
            finalHeight = height
        }

        shapeRenderer.color =
            Color(
                colorR,
                colorG,
                colorB,
                colorA
            )

        shapeRenderer.rect(
            screenX,
            screenY,
            finalWidth,
            finalHeight
        )
    }

    fun end() {

        shapeRenderer.end()

        camera = null
    }

    fun dispose() {

        shapeRenderer.dispose()
    }
}