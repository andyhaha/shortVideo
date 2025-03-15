package com.andy.videolist.txplayer

import com.andy.videolist.ui.TXVideoListAdapter
import com.tencent.rtmp.TXVodPlayer

/**
 * TXVodPlayer的包装类，包含TXVodPlayer、VideoViewHolder和isManualPaused
 */
data class TXVodPlayerWrapper(
    val player: TXVodPlayer,
    val holder: TXVideoListAdapter.VideoViewHolder,
    // 用户手动暂停标志位
    var isManualPaused: Boolean = false
)