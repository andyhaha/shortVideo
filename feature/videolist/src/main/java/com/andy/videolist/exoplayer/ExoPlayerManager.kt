package com.andy.videolist.exoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object ExoPlayerManager {
    var simpleCache: SimpleCache? = null
        private set

    @OptIn(UnstableApi::class)
    fun init(context: Context) {
        val cacheDir = File(context.cacheDir, "exo_video")
        simpleCache = SimpleCache(
            cacheDir,
            // 200M 缓存
            LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024L),
            StandaloneDatabaseProvider(context)
        )
    }
}