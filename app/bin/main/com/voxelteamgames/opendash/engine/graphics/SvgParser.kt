package com.voxelteamgames.opendash.engine.graphics

import java.io.InputStream

class SvgParser {

    fun parse(input: InputStream): VectorShape {

        val svg = input.bufferedReader().use {
            it.readText()
        }

        val pathStart = svg.indexOf("<path")

        require(pathStart >= 0) {
            "SVG não contém um elemento <path>."
        }

        val pathEnd = svg.indexOf(">", pathStart)

        require(pathEnd >= 0) {
            "Elemento <path> inválido."
        }

        val pathTag = svg.substring(
            pathStart,
            pathEnd
        )

        val dStart = pathTag.indexOf("d=\"")

        require(dStart >= 0) {
            "Elemento <path> não possui atributo d."
        }

        val dValueStart = dStart + 3

        val dValueEnd = pathTag.indexOf(
            "\"",
            dValueStart
        )

        require(dValueEnd >= 0) {
            "Atributo d inválido."
        }

        val pathData = pathTag.substring(
            dValueStart,
            dValueEnd
        )

        return parsePath(pathData)
    }

    private fun parsePath(
        data: String
    ): VectorShape {

        val tokens = tokenize(data)

        val vertices = mutableListOf<Float>()

        var index = 0

        var currentX = 0f
        var currentY = 0f

        var startX = 0f
        var startY = 0f

        while (index < tokens.size) {

            val command = tokens[index++]

            when (command) {

                "M" -> {
                    currentX = tokens[index++].toFloat()
                    currentY = tokens[index++].toFloat()

                    startX = currentX
                    startY = currentY

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "m" -> {
                    currentX += tokens[index++].toFloat()
                    currentY += tokens[index++].toFloat()

                    startX = currentX
                    startY = currentY

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "L" -> {
                    currentX = tokens[index++].toFloat()
                    currentY = tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "l" -> {
                    currentX += tokens[index++].toFloat()
                    currentY += tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "H" -> {
                    currentX = tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "h" -> {
                    currentX += tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "V" -> {
                    currentY = tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "v" -> {
                    currentY += tokens[index++].toFloat()

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                "C" -> {

                    val x1 = tokens[index++].toFloat()
                    val y1 = tokens[index++].toFloat()

                    val x2 = tokens[index++].toFloat()
                    val y2 = tokens[index++].toFloat()

                    val x3 = tokens[index++].toFloat()
                    val y3 = tokens[index++].toFloat()

                    addCubicBezier(
                        vertices,
                        currentX,
                        currentY,
                        x1,
                        y1,
                        x2,
                        y2,
                        x3,
                        y3
                    )

                    currentX = x3
                    currentY = y3
                }

                "c" -> {

                    val x1 = currentX + tokens[index++].toFloat()
                    val y1 = currentY + tokens[index++].toFloat()

                    val x2 = currentX + tokens[index++].toFloat()
                    val y2 = currentY + tokens[index++].toFloat()

                    val x3 = currentX + tokens[index++].toFloat()
                    val y3 = currentY + tokens[index++].toFloat()

                    addCubicBezier(
                        vertices,
                        currentX,
                        currentY,
                        x1,
                        y1,
                        x2,
                        y2,
                        x3,
                        y3
                    )

                    currentX = x3
                    currentY = y3
                }

                "Z", "z" -> {

                    currentX = startX
                    currentY = startY

                    vertices.add(currentX)
                    vertices.add(currentY)
                }

                else -> {
                    error(
                        "Comando SVG não suportado: $command"
                    )
                }
            }
        }

        return normalize(vertices)
    }

    private fun addCubicBezier(
        vertices: MutableList<Float>,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float
    ) {

        val segments = 24

        for (i in 1..segments) {

            val t = i.toFloat() / segments
            val u = 1f - t

            val x =
                u * u * u * x0 +
                3f * u * u * t * x1 +
                3f * u * t * t * x2 +
                t * t * t * x3

            val y =
                u * u * u * y0 +
                3f * u * u * t * y1 +
                3f * u * t * t * y2 +
                t * t * t * y3

            vertices.add(x)
            vertices.add(y)
        }
    }

    private fun tokenize(
        data: String
    ): List<String> {

        return Regex(
            """[MmLlHhVvCcZz]|[-+]?(?:\d*\.\d+|\d+\.?)(?:[eE][-+]?\d+)?"""
        )
            .findAll(data)
            .map {
                it.value
            }
            .toList()
    }

    private fun normalize(
        vertices: List<Float>
    ): VectorShape {

        require(vertices.isNotEmpty()) {
            "SVG não possui vértices."
        }

        var minX = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY

        var minY = Float.POSITIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY

        for (i in vertices.indices step 2) {

            val x = vertices[i]
            val y = vertices[i + 1]

            minX = minOf(minX, x)
            maxX = maxOf(maxX, x)

            minY = minOf(minY, y)
            maxY = maxOf(maxY, y)
        }

        val centerX = (minX + maxX) / 2f
        val centerY = (minY + maxY) / 2f

        val width = maxX - minX
        val height = maxY - minY

        require(width > 0f) {
            "SVG possui largura zero."
        }

        require(height > 0f) {
            "SVG possui altura zero."
        }

        val normalized = FloatArray(
            vertices.size
        )

        for (i in vertices.indices step 2) {

            normalized[i] =
                (vertices[i] - centerX) / width

            normalized[i + 1] =
                (vertices[i + 1] - centerY) / height
        }

        return VectorShape(
            normalized
        )
    }
}