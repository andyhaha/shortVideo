package com.andy.flexplayer.exoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.andy.flexplayer.databinding.ActivityVideoBinding
import java.io.File

@UnstableApi
class DemoPlayerActivity : ComponentActivity() {

    private lateinit var binding: ActivityVideoBinding


    private val exoPlayer by lazy {
//        val cache = SimpleCache(
//            File(cacheDir, "exo_cache"),
//            LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024), // 设置最大缓存 100MB
//            StandaloneDatabaseProvider(this)
//        )
//        val cacheDataSourceFactory = CacheDataSource.Factory()
//            .setCache(cache)
//            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
//            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
//        val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(5_000, 15_000, 2_000, 2_000)
            .setTargetBufferBytes(C.LENGTH_UNSET)  // 目标缓冲大小
            .setPrioritizeTimeOverSizeThresholds(true)  // 优先考虑时间
            .build()

        ExoPlayer.Builder(this)
//            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri("https://video.wasai-app-dev.com/30693ff3-f3f0-44c5-916c-7aae01c79497/hls/1738807997.687389outputEditCut_1080x1920p_qvbr.m3u8")
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.prepare()
        exoPlayer.play()
//        exoPlayer.playWhenReady = true
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}