package com.andy.flexplayer.txvodplayer

import android.content.Context
import com.andy.flexplayer.CacheConfig
import java.io.File

class TXVodPlayerCacheConfig(
    private val context: Context,
    override val cacheDirName: String,
    override val cacheSize: Int,
) : CacheConfig<File> {

    override val cache: File by lazy {
        getCacheDir()
    }

    private fun getCacheDir(): File {
        val externalCacheDir = context.getExternalFilesDir(null)
        // 如果外部存储可用，使用外部存储目录，否则使用内部存储目录
        return File(externalCacheDir ?: context.cacheDir, cacheDirName)
    }
}