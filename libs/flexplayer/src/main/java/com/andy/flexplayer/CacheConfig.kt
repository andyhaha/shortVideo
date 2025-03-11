package com.andy.flexplayer

interface CacheConfig<T> {
    val cacheDirName: String
    // 单位是MB
    val cacheSize: Int
    val cache: T
}