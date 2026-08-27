package com.voxelteamgames.opendash.engine.graphics

data class VectorShape(
    val vertices: FloatArray
) {

    init {

        require(
            vertices.size % 2 == 0
        ) {
            "A quantidade de coordenadas dos vértices deve ser par."
        }
    }

    val vertexCount: Int
        get() = vertices.size / 2
}