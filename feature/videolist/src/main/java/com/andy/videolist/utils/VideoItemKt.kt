package com.andy.videolist.utils

import android.content.Context
import com.andy.common.NetworkUtil
import com.andy.videolist.domain.model.VideoItem

/**
 * 根据当前网络状态选择合适的视频 URL。
 * - 如果当前连接的是 Wi-Fi，则返回高清（HD）视频 URL。
 * - 否则，返回普通视频 URL，以减少移动数据消耗，提升视频流畅性。
 *
 * @param context 上下文对象，用于检查网络状态
 * @return 适合当前网络环境的视频 URL
 */
fun VideoItem.getAdaptiveVideoUrl(context: Context): String = when {
    NetworkUtil.isConnectedToWifi(context) -> videoUrlHd
    NetworkUtil.isNetworkUnavailable(context) -> videoUrlHd
    else -> videoUrl
}

