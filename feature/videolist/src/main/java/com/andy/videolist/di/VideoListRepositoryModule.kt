package com.andy.videolist.di

import com.andy.videolist.data.VideoListRepositoryImpl
import com.andy.videolist.domain.repository.VideoListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoListRepositoryModule {

    @Binds
    abstract fun bindVideoListRepository(
        repository: VideoListRepositoryImpl,
    ): VideoListRepository
}