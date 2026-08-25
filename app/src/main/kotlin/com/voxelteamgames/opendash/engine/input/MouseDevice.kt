package com.voxelteamgames.opendash.engine.input

class MouseDevice(
    override val id: String,
    override val name: String
) : InputDevice {

    var x: Double = 0.0
        private set

    var y: Double = 0.0
        private set

    fun setPosition(x: Double, y: Double) {
        this.x = x
        this.y = y
    }
}