package com.andy.shortvideo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.andy.shortvideo.databinding.ActivityMainBinding
import com.andy.videolist.ui.VideoListActivity
import com.tencent.rtmp.ITXVodPlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXVodPlayConfig
import com.tencent.rtmp.TXVodPlayer


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val playUrl =
        "https://video.wasai-app-dev.com/0224cf3e-2ac7-46a5-b44e-c3c66a2abe91/hls/TXVideo_20250304_153759_66c05f66-33e8-4419-bd5f-aa2a37fe4ef8_720x1280p_qvbr.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOpenVideoList.setOnClickListener {
            startActivity(
                Intent(this, VideoListActivity::class.java)
            )
        }
    }

}