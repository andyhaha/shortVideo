package com.andy.videolist.exoplayer

import android.content.Context
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.playlist.DefaultHlsPlaylistParserFactory
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

/**
 * ExoPlayerSupplier 用于提供 ExoPlayer 实例。
 */
@UnstableApi
object ExoPlayerSupplier {
    private const val TAG = "ExoPlayer"
    private const val MIN_BUFFER_MS = 1_000  // 最小缓冲时间（1秒）
    private const val MAX_BUFFER_MS = 15_000 // 最大缓冲时间（15秒）
    private const val BUFFER_FOR_PLAYBACK_MS = 1_000 // 播放所需缓冲时间（1秒）
    private const val BUFFER_FOR_PLAYBACK_AFTER_RE_BUFFER_MS = 1_000 // 重新缓冲后所需时间（1秒）

    fun initializeExoPlayer(
        context: Context,
        videoUrl: String,
        onPlaying: () -> Unit
    ): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(MIN_BUFFER_MS, MAX_BUFFER_MS, BUFFER_FOR_PLAYBACK_MS, BUFFER_FOR_PLAYBACK_AFTER_RE_BUFFER_MS)
            .setTargetBufferBytes(C.LENGTH_UNSET) // 目标缓冲大小
            .setPrioritizeTimeOverSizeThresholds(true)  // 优先考虑时间
            .build()

        val cache = ExoPlayerManager.simpleCache!!
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setCacheWriteDataSinkFactory(
                CacheDataSink.Factory()
                    .setCache(cache)
                    .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
            )
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setStreamKeys(
                listOf(StreamKey(HlsMultivariantPlaylist.GROUP_INDEX_VARIANT, 0))
            )
            .build()

        val mediaSource = HlsMediaSource.Factory(cacheDataSourceFactory)
            .setAllowChunklessPreparation(true)
            .setPlaylistParserFactory(DefaultHlsPlaylistParserFactory())
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
                        Log.d(TAG, "onPlaybackStateChanged() state: $state")
                        when (state) {
                            Player.STATE_IDLE -> {
                                Log.d(TAG, "Player is idle")
                            }

                            Player.STATE_BUFFERING -> {
                                Log.d(TAG, "Player is buffering")
                            }

                            Player.STATE_READY -> {
                                Log.d(TAG, "Player is ready to play")
                                onPlaying()
                            }

                            Player.STATE_ENDED -> {
                                Log.d(TAG, "Playback ended")
                            }
                        }
                    }

                    override fun onRenderedFirstFrame() {
                        Log.d(TAG, "onRenderedFirstFrame()")
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "Playback error: ${error.message}")
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            Log.d(TAG, "Player is playing")
                            onPlaying()
                        } else {
                            Log.d(TAG, "Player is paused")
                        }
                    }
                })
            }
    }
}