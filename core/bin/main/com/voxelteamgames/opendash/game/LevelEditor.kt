package com.voxelteamgames.opendash.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.voxelteamgames.opendash.engine.graphics.Camera
import com.voxelteamgames.opendash.engine.graphics.SpriteRenderer
import com.voxelteamgames.opendash.engine.graphics.Texture
import com.voxelteamgames.opendash.engine.graphics.VectorRenderer
import com.voxelteamgames.opendash.engine.graphics.VectorShape
import kotlin.math.floor

class LevelEditor(
    private var level: Level
) {

    var number = 1

    var selectedObject: LevelObject? = null
        private set

    var selectedObjectId: String = "block.iron"
        private set

    private var leftMouseWasPressed = false
    private var rightMouseWasPressed = false
    private var deleteWasPressed = false

    private var dragging = false

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    var cursorX = 0f
        private set

    var cursorY = 0f
        private set

    companion object {
        const val GRID_SIZE = 64f
    }

    // =================================================
    // UPDATE
    // =================================================

    fun update(
        camera: Camera,
        deltaTime: Float
    ) {

        val cameraSpeed =
            500f * deltaTime

        // =================================================
        // SELEÇÃO DA FERRAMENTA
        // =================================================

        if (
            Gdx.input.isKeyPressed(Input.Keys.NUM_1)
        ) {
            selectedObjectId = "block.iron"
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.NUM_2)
        ) {
            selectedObjectId = "hazard.yellow_spike"
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.NUM_3)
        ) {
            selectedObjectId = "trigger.portal_cube"
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.NUM_4)
        ) {
            selectedObjectId = "trigger.portal_ship"
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.NUM_5)
        ) {
            selectedObjectId = "trigger.end"
        }

        // =================================================
        // MOVIMENTO DA CÂMERA
        // =================================================

        if (
            Gdx.input.isKeyPressed(Input.Keys.A)
        ) {
            camera.x -= cameraSpeed
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.D)
        ) {
            camera.x += cameraSpeed
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.W)
        ) {
            camera.y -= cameraSpeed
        }

        if (
            Gdx.input.isKeyPressed(Input.Keys.S)
        ) {
            camera.y += cameraSpeed
        }

        // =================================================
        // MOUSE
        // =================================================

        /*
         * O LibGDX usa origem no canto superior esquerdo
         * para as coordenadas do mouse.
         *
         * Nosso mundo usa a mesma orientação vertical
         * do renderer antigo, então convertemos Y usando
         * a altura da tela.
         */

        val mouseX =
            Gdx.input.x.toFloat()

        val mouseY =
            Gdx.graphics.height -
            Gdx.input.y.toFloat()

        // =================================================
        // SCREEN -> WORLD
        // =================================================

        val worldX =
            camera.screenToWorldX(
                mouseX
            )

        val worldY =
            camera.screenToWorldY(
                mouseY
            )

        cursorX =
            snap(worldX)

        cursorY =
            snap(worldY)

        // =================================================
        // DELETE
        // =================================================

        val deletePressed =
            Gdx.input.isKeyPressed(
                Input.Keys.FORWARD_DEL
            ) ||
            Gdx.input.isKeyPressed(
                Input.Keys.DEL
            )

        if (
            deletePressed &&
            !deleteWasPressed
        ) {
            deleteSelected()
        }

        deleteWasPressed =
            deletePressed

        // =================================================
        // BOTÃO ESQUERDO
        // =================================================

        val leftPressed =
            Gdx.input.isButtonPressed(
                Input.Buttons.LEFT
            )

        // =================================================
        // INÍCIO DO CLIQUE
        // =================================================

        if (
            leftPressed &&
            !leftMouseWasPressed
        ) {

            val levelObjectUnderMouse =
                findObject(
                    worldX,
                    worldY
                )

            if (
                levelObjectUnderMouse != null
            ) {

                selectedObject =
                    levelObjectUnderMouse

                dragging = true

                dragOffsetX =
                    worldX -
                    levelObjectUnderMouse.x

                dragOffsetY =
                    worldY -
                    levelObjectUnderMouse.y

            } else {

                val newObject =
                    LevelObject(
                        id = selectedObjectId,
                        x = cursorX,
                        y = cursorY,
                        scaleX = 1f,
                        scaleY = 1f,
                        rotation = 0f
                    )

                level.objects.add(
                    newObject
                )

                selectedObject =
                    newObject
            }
        }

        // =================================================
        // ARRASTAR
        // =================================================

        if (
            leftPressed &&
            dragging &&
            selectedObject != null
        ) {

            val selected =
                selectedObject!!

            selected.x =
                snap(
                    worldX -
                    dragOffsetX
                )

            selected.y =
                snap(
                    worldY -
                    dragOffsetY
                )
        }

        // =================================================
        // SOLTOU
        // =================================================

        if (
            !leftPressed &&
            leftMouseWasPressed
        ) {
            dragging = false
        }

        leftMouseWasPressed =
            leftPressed

        // =================================================
        // BOTÃO DIREITO
        // =================================================

        val rightPressed =
            Gdx.input.isButtonPressed(
                Input.Buttons.RIGHT
            )

        if (
            rightPressed &&
            !rightMouseWasPressed
        ) {

            val levelObjectUnderMouse =
                findObject(
                    worldX,
                    worldY
                )

            if (
                levelObjectUnderMouse != null
            ) {

                if (
                    selectedObject ===
                    levelObjectUnderMouse
                ) {
                    selectedObject = null
                }

                level.objects.remove(
                    levelObjectUnderMouse
                )
            }
        }

        rightMouseWasPressed =
            rightPressed
    }

    // =================================================
    // SNAP
    // =================================================

    private fun snap(
        value: Float
    ): Float {

        return floor(
            value / GRID_SIZE
        ) * GRID_SIZE
    }

    // =================================================
    // ENCONTRAR OBJETO
    // =================================================

    private fun findObject(
        x: Float,
        y: Float
    ): LevelObject? {

        for (
            i in level.objects.indices.reversed()
        ) {

            val levelObject =
                level.objects[i]

            val width =
                GRID_SIZE *
                levelObject.scaleX

            val height =
                GRID_SIZE *
                levelObject.scaleY

            if (
                x >= levelObject.x &&
                x < levelObject.x + width &&
                y >= levelObject.y &&
                y < levelObject.y + height
            ) {
                return levelObject
            }
        }

        return null
    }

    // =================================================
    // DELETE
    // =================================================

    private fun deleteSelected() {

        val selected =
            selectedObject
                ?: return

        level.objects.remove(
            selected
        )

        selectedObject = null
        dragging = false
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

        for (
            levelObject in level.objects
        ) {

            val definition =
                ObjectRegistry.get(
                    levelObject.id
                )

            when (
                definition.type
            ) {

                // =================================================
                // BLOCK / HAZARD
                // =================================================

                ObjectType.BLOCK,
                ObjectType.HAZARD -> {

                    if (
                        definition.texture != null
                    ) {

                        val texture =
                            textures[
                                definition.texture
                            ] ?: continue

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            GRID_SIZE *
                                levelObject.scaleX,
                            GRID_SIZE *
                                levelObject.scaleY
                        )

                    } else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ] ?: continue

                        vectorRenderer.draw(
                            shape = shape,
                            x = levelObject.x,
                            y = levelObject.y,
                            scaleX =
                                GRID_SIZE *
                                levelObject.scaleX,
                            scaleY =
                                GRID_SIZE *
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
                        ] ?: continue

                    vectorRenderer.draw(
                        shape = shape,
                        x = levelObject.x,
                        y = levelObject.y,
                        scaleX =
                            GRID_SIZE *
                            levelObject.scaleX,
                        scaleY =
                            GRID_SIZE *
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
                            ] ?: continue

                        renderer.draw(
                            texture,
                            levelObject.x,
                            levelObject.y,
                            GRID_SIZE *
                                levelObject.scaleX,
                            GRID_SIZE *
                                levelObject.scaleY
                        )

                    } else if (
                        definition.vector != null
                    ) {

                        val shape =
                            vectors[
                                definition.vector
                            ] ?: continue

                        vectorRenderer.draw(
                            shape = shape,
                            x = levelObject.x,
                            y = levelObject.y,
                            scaleX =
                                GRID_SIZE *
                                levelObject.scaleX,
                            scaleY =
                                GRID_SIZE *
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
    }

    // =================================================
    // GRID
    // =================================================

    fun renderGrid(
        vectorRenderer: VectorRenderer,
        camera: Camera
    ) {

        val left =
            camera.x -
            camera.width / 2f

        val right =
            camera.x +
            camera.width / 2f

        val top =
            camera.y -
            camera.height / 2f

        val bottom =
            camera.y +
            camera.height / 2f

        val startX =
            floor(
                left / GRID_SIZE
            ) * GRID_SIZE

        val startY =
            floor(
                top / GRID_SIZE
            ) * GRID_SIZE

        var x = startX

        while (
            x <= right
        ) {

            vectorRenderer.drawRect(
                x = x,
                y = top,
                width = 1f,
                height = bottom - top,
                colorR = 1f,
                colorG = 1f,
                colorB = 1f,
                colorA = 0.10f
            )

            x += GRID_SIZE
        }

        var y = startY

        while (
            y <= bottom
        ) {

            vectorRenderer.drawRect(
                x = left,
                y = y,
                width = right - left,
                height = 1f,
                colorR = 1f,
                colorG = 1f,
                colorB = 1f,
                colorA = 0.10f
            )

            y += GRID_SIZE
        }
    }

    // =================================================
    // CURSOR
    // =================================================

    fun renderCursor(
        vectorRenderer: VectorRenderer
    ) {

        vectorRenderer.drawRect(
            x = cursorX,
            y = cursorY,
            width = GRID_SIZE,
            height = GRID_SIZE,
            colorR = 1f,
            colorG = 1f,
            colorB = 1f,
            colorA = 0.15f
        )
    }

    // =================================================
    // SELEÇÃO
    // =================================================

    fun renderSelection(
        vectorRenderer: VectorRenderer
    ) {

        val selected =
            selectedObject
                ?: return

        vectorRenderer.drawRect(
            x = selected.x,
            y = selected.y,
            width =
                GRID_SIZE *
                selected.scaleX,
            height =
                GRID_SIZE *
                selected.scaleY,
            colorR = 0.6f,
            colorG = 0.8f,
            colorB = 1f,
            colorA = 0.25f
        )
    }
}