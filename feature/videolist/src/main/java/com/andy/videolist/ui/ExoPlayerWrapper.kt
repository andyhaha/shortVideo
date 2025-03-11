package com.andy.videolist.ui

import androidx.media3.exoplayer.ExoPlayer

/**
 * TXVodPlayer的包装类，包含TXVodPlayer、VideoViewHolder和isManualPaused
 */
data class ExoPlayerWrapper(
    val player: ExoPlayer,
    val holder: ExoVideoListAdapter.VideoViewHolder,
    // 用户手动暂停标志位
    var isManualPaused: Boolean = false
)