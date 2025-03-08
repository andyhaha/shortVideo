package com.andy.videolist.txplayer

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.andy.videolist.R
import com.tencent.rtmp.ITXVodPlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXVodPlayConfig
import com.tencent.rtmp.TXVodPlayer

/**
 * TXVodPlayer的提供者 负责创建TXVodPlayer
 * TXVodPlayer相关文档请参考：https://cloud.tencent.com/document/product/881/20216
 */
object TXVodPlayerSupplier {

    fun createPlayer(
        context: Context,
        onPlayStart: () -> Unit = {},
        onPlayError: (error: String, code: Int) -> Unit = { _, _ -> },
    ): TXVodPlayer {
        return TXVodPlayer(context).apply {
//            setPlayerView(binding.videoView)
            // 设置循环播放
            isLoop = true
            // 正常播放（Home 键在画面正下方）
            setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
            // 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
//            setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)
            // 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全 。
            setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN)
            // 11、码流自适应
            // SDK 支持 HLS 的多码流自适应，开启相关能力后播放器能够根据当前带宽，
            // 动态选择最合适的码率播放。可以通过下面方法开启码流自适应。
            bitrateIndex = -1

            val playConfig = TXVodPlayConfig()
            // 在视频正常播放时，控制提前从网络缓冲的最大数据大小。如果不配置，则走播放器默认缓冲策略，保证流畅播放。
            // 设为true，在IDR对齐时可平滑切换码率, 设为false时，可提高多码率地址打开速度
            playConfig.isSmoothSwitchBitrate = true
            // 播放时最大缓冲大小。单位：MB
            playConfig.maxBufferSize = 10.0f
            // 此接口针对预加载场景（即在视频启播前，且设置 player 的 AutoPlay 为 false），
            // 用于控制启播前阶段的最大缓冲大小。
            // 预播放最大缓冲大小。单位：MB, 根据业务情况设置去节省流量
            playConfig.maxPreloadSize = 2.0f
            setConfig(playConfig)

            setVodListener(object : ITXVodPlayListener {
                override fun onPlayEvent(player: TXVodPlayer, event: Int, param: Bundle) {
                    Log.d("TXVodPlayer", "onPlayEvent: $event")
                    when (event) {
                        TXLiveConstants.PLAY_EVT_PLAY_LOADING -> {
                            // 显示当前网速
                        }

                        TXLiveConstants.PLAY_EVT_VOD_LOADING_END -> {
                            // 对显示当前网速的 view 进行隐藏
                        }

                        TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME,
                        TXLiveConstants.PLAY_EVT_PLAY_BEGIN,
                        TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION -> {
                            onPlayStart()
                        }

                        TXLiveConstants.PLAY_ERR_NET_DISCONNECT -> {
                            // 视频数据错误导致重试亦不能恢复正常播放。如：网络异常或下载数据错误，导致解封装超时或失败。
                            onPlayError(
                                context.getString(R.string.network_disconnected),
                                TXLiveConstants.PLAY_ERR_NET_DISCONNECT
                            )
                        }
                    }
                }

                override fun onNetStatus(player: TXVodPlayer, bundle: Bundle) {
                    //获取当前CPU使用率
                    val cpuUsage = bundle.getCharSequence(TXLiveConstants.NET_STATUS_CPU_USAGE)

                    //获取视频宽度
                    val videoWidth = bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)

                    //获取视频高度
                    val videoHeight = bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)

                    //获取实时速率,  单位：kbps
                    val speed = bundle.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)

                    //获取当前流媒体的视频帧率
                    val fps = bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)

                    //获取当前流媒体的视频码率，单位 bps
                    val videoBitRate = bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)

                    //获取当前流媒体的音频码率，单位 bps
                    val audioBitRate = bundle.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)

                    //获取缓冲区（jitterBuffer）大小，缓冲区当前长度为0，说明离卡顿就不远了
                    val jitterBuffer = bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE)

                    //获取连接的服务器的IP地址
                    val ip = bundle.getString(TXLiveConstants.NET_STATUS_SERVER_IP)

                    Log.d(
                        "TXVodPlayer", "onNetStatus: cpuUsage=$cpuUsage, " +
                                "videoWidth=$videoWidth, " + "videoHeight=$videoHeight, " +
                                "speed=$speed, " + "fps=$fps, videoBitRate=$videoBitRate, " +
                                "audioBitRate=$audioBitRate, " + "jitterBuffer=$jitterBuffer, ip=$ip"
                    )
                }
            })
        }
    }
}