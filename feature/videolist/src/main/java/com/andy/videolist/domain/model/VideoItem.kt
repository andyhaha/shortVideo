package com.andy.videolist.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoItem(
    val coverUrl: String,
    // 720p视频源 例如： https://video.wasai-app-dev.com/30693ff3-f3f0-44c5-916c-7aae01c79497/hls/1738807997.687389outputEditCut_720x1280p_qvbr.m3u8
    @Json(name = "mediaUrl")
    val videoUrl: String,
    // 1080p视频源 例如：https://video.wasai-app-dev.com/30693ff3-f3f0-44c5-916c-7aae01c79497/hls/1738807997.687389outputEditCut_1080x1920p_qvbr.m3u8
    @Json(name = "mediaUrlHd")
    val videoUrlHd: String
)