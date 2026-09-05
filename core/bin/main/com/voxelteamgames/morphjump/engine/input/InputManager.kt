package com.voxelteamgames.morphjump.engine.input

class InputManager {

    private val _devices = mutableListOf<InputDevice>()

    val devices: List<InputDevice>
        get() = _devices

    fun addDevice(device: InputDevice) {
        _devices.add(device)
    }

    fun removeDevice(device: InputDevice) {
        _devices.remove(device)
    }

    fun <T : InputDevice> devicesOfType(type: Class<T>): List<T> {
        return _devices.filter { type.isInstance(it) }
            .map { type.cast(it) }
    }
}