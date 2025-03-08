package com.andy.videolist.data

import com.andy.videolist.domain.model.VideoItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoListBean(
    val data: List<VideoItem>
)
