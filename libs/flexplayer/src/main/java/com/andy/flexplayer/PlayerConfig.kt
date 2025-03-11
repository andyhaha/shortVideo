package com.andy.flexplayer

import androidx.annotation.IntDef

data class PlayerConfig<T>(
    // 默认 ExoPlayer
    val playerType: Int = PlayerType.EXOPLAYER,
    // 默认缓存策略
    val cacheConfig: CacheConfig<T>
)

// 定义播放器类型
@IntDef(PlayerType.EXOPLAYER, PlayerType.TX_VOD_PLAYER)
@Retention(AnnotationRetention.SOURCE)
annotation class PlayerType {
    companion object {
        const val EXOPLAYER = 0
        const val TX_VOD_PLAYER = 1
    }
}


