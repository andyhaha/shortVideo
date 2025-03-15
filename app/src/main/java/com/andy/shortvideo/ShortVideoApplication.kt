package com.andy.shortvideo

import android.app.Application
import androidx.media3.common.util.UnstableApi
import com.andy.videolist.exoplayer.ExoPlayerManager
import com.andy.videolist.txplayer.TXLiveSdkManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ShortVideoApplication : Application() {
    companion object {
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        TXLiveSdkManager.init(this)
        ExoPlayerManager.init(this)
    }
}