package com.voxelteamgames.opendash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

import com.voxelteamgames.opendash.game.LevelStorage

class AndroidLauncher : AndroidApplication() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        if (
            !Environment.isExternalStorageManager()
        ) {

            val intent =
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse(
                        "package:$packageName"
                    )
                )

            startActivity(intent)

            return
        }

        LevelStorage.provider =
            AndroidLevelDirectoryProvider()

        val config =
            AndroidApplicationConfiguration()

        initialize(
            OpenDashGame(),
            config
        )
    }
}