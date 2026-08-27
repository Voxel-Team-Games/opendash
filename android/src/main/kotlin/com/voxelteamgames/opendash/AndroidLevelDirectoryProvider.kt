package com.voxelteamgames.opendash

import com.voxelteamgames.opendash.game.LevelDirectoryProvider

class AndroidLevelDirectoryProvider :
    LevelDirectoryProvider {

    override fun getLevelsDirectory(): String {

        return AndroidStorage
            .levelsDirectory()
            .absolutePath
    }
}