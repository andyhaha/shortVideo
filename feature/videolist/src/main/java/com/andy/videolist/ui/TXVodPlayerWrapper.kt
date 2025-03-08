package com.andy.videolist.ui

import com.tencent.rtmp.TXVodPlayer

/**
 * TXVodPlayer的包装类，包含TXVodPlayer、VideoViewHolder和isManualPaused
 */
data class TXVodPlayerWrapper(
    val player: TXVodPlayer,
    val holder: VideoListAdapter.VideoViewHolder,
    // 用户手动暂停标志位
    var isManualPaused: Boolean = false,
    // 记录当前播放的时间或位置
    var currentPlaybackTime: Float = 0f
)