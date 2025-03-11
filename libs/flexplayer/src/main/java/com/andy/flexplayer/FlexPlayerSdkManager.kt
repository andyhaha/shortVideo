package com.andy.flexplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

object FlexPlayerSdkManager {
//    private var config: PlayerConfig = PlayerConfig()

    @OptIn(UnstableApi::class)
    fun init(
        context: Context,
//        playerConfig: PlayerConfig = PlayerConfig()
    ) {
        val appContext = context.applicationContext
//        config = playerConfig
        val cache = SimpleCache(
            File(appContext.cacheDir, "exo_cache"),
            // 设置最大缓存 100MB
            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024),
            StandaloneDatabaseProvider(appContext)
        )
    }

//    fun getConfig(): PlayerConfig = config
}
