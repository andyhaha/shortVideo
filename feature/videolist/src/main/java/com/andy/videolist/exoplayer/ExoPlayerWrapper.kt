package com.andy.videolist.exoplayer

import androidx.media3.exoplayer.ExoPlayer
import com.andy.videolist.ui.ExoVideoListAdapter

/**
 * ExoPlayer的包装类，包含ExoPlayer、VideoViewHolder和isManualPaused
 */
data class ExoPlayerWrapper(
    val player: ExoPlayer,
    val holder: ExoVideoListAdapter.VideoViewHolder,
    // 用户手动暂停标志位
    var isManualPaused: Boolean = false
)