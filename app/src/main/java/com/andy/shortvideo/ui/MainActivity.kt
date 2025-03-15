package com.andy.shortvideo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.andy.shortvideo.databinding.ActivityMainBinding
import com.andy.videolist.ui.VideoListActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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