package com.andy.shortvideo

import android.app.Application
import com.andy.videolist.txplayer.TXLiveSdkManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ShortVideoApplication : Application() {
    companion object {
    }

    override fun onCreate() {
        super.onCreate()
        TXLiveSdkManager.init(this)
    }
}