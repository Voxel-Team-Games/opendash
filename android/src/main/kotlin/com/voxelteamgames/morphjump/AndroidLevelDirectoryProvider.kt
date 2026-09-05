package com.voxelteamgames.morphjump

import com.voxelteamgames.morphjump.game.LevelDirectoryProvider

class AndroidLevelDirectoryProvider :
    LevelDirectoryProvider {

    override fun getLevelsDirectory(): String {

        return AndroidStorage
            .levelsDirectory()
            .absolutePath
    }
}