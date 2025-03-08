package com.andy.videolist.domain.repository

import com.andy.videolist.domain.model.VideoItem
import kotlinx.coroutines.flow.Flow

interface VideoListRepository {
    fun getVideoList(): Flow<List<VideoItem>>
}
