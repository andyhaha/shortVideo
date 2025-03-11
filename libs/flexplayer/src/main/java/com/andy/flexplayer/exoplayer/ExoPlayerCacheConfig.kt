package com.andy.flexplayer.exoplayer

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.andy.flexplayer.CacheConfig
import java.io.File

@UnstableApi
class ExoPlayerCacheConfig(
    context: Context,
    override val cacheDirName: String = "exo_cache",
    override val cacheSize: Int = 200
) : CacheConfig<SimpleCache> {

    override val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, cacheDirName)
        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(cacheSize * 1024 * 1024L), // 转换为字节
            StandaloneDatabaseProvider(context)
        )
    }
}
