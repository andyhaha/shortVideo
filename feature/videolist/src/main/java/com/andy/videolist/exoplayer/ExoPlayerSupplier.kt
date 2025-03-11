package com.andy.videolist.exoplayer

import android.content.Context
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@UnstableApi
object ExoPlayerSupplier {
    private const val TAG = "ExoPlayerSupplier"

    fun createPlayer(
        context: Context,
        onPlayStart: () -> Unit = {}
    ): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(500, 10_000, 500, 500)
            .setTargetBufferBytes(C.LENGTH_UNSET) // 目标缓冲大小
            .setPrioritizeTimeOverSizeThresholds(true)  // 优先考虑时间
            .build()

        val exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .also { builder ->
                ExoPlayerManager.simpleCache?.let {
                    val cacheDataSourceFactory = CacheDataSource.Factory()
                        .setCache(it)
                        .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                    val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)
                    builder.setMediaSourceFactory(mediaSourceFactory)
                }
            }
            .build().also {
                it.repeatMode = Player.REPEAT_MODE_ONE
            }
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.d(TAG, "onPlaybackStateChanged() state: $state")
                when (state) {
                    Player.STATE_IDLE -> {
                        // 播放器处于空闲状态
                    }

                    Player.STATE_BUFFERING -> {
                        // 播放器缓冲中
                    }

                    Player.STATE_READY -> {
                        // 播放器准备好，可以播放
                        onPlayStart()
                    }

                    Player.STATE_ENDED -> {
                        // 播放结束
                    }
                }
            }

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                Log.d(TAG, "onRenderedFirstFrame()")
            }

            override fun onPlayerError(error: PlaybackException) {
                // 处理播放错误
                Log.e(TAG, "Playback error: ${error.message}")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 播放状态发生变化（正在播放/暂停）
                if (isPlaying) {
                    Log.d(TAG, "Player is playing")
                    onPlayStart()
                } else {
                    Log.d(TAG, "Player is paused")
                }
            }
        })
        return exoPlayer
    }

    fun initializeExoPlayer(
        context: Context,
        videoUrl: String,
        onPlayStart: () -> Unit
    ): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(500, 10_000, 500, 500)
            .setTargetBufferBytes(C.LENGTH_UNSET) // 目标缓冲大小
            .setPrioritizeTimeOverSizeThresholds(true)  // 优先考虑时间
            .build()

        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(ExoPlayerManager.simpleCache!!)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)

        val mediaItem = MediaItem.fromUri(videoUrl)

        val mediaSource = HlsMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(mediaItem)

        return ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            setMediaSource(mediaSource, false)
            prepare()

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    Log.d("ExoPlayer", "onPlaybackStateChanged() state: $state")
                    when (state) {
                        Player.STATE_IDLE -> {
                            Log.d("ExoPlayer", "Player is idle")
                        }

                        Player.STATE_BUFFERING -> {
                            Log.d("ExoPlayer", "Player is buffering")
                        }

                        Player.STATE_READY -> {
                            Log.d("ExoPlayer", "Player is ready to play")
                            onPlayStart()
                        }

                        Player.STATE_ENDED -> {
                            Log.d("ExoPlayer", "Playback ended")
                        }
                    }
                }

                override fun onRenderedFirstFrame() {
                    Log.d("ExoPlayer", "onRenderedFirstFrame()")
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e("ExoPlayer", "Playback error: ${error.message}")
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        Log.d("ExoPlayer", "Player is playing")
                        onPlayStart()
                    } else {
                        Log.d("ExoPlayer", "Player is paused")
                    }
                }
            })
        }
    }
}