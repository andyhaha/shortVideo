package com.andy.videolist.ui

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.andy.videolist.databinding.ActivityVideoListBinding
import com.andy.videolist.domain.model.VideoItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoListActivity : ComponentActivity() {

    private lateinit var binding: ActivityVideoListBinding
    private lateinit var videoListAdapter: ExoVideoListAdapter

    private val viewModel: VideoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewPager()
        registerLifeCycleObserver()
        collectUiState()
        fetchVideoList()
    }

    private fun initViewPager() {
        videoListAdapter = ExoVideoListAdapter()
        binding.viewPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit = 3
            adapter = videoListAdapter
        }

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    videoListAdapter.onPageSelected(position)
                }
            }
        )
    }

    private fun registerLifeCycleObserver() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                videoListAdapter.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                videoListAdapter.onPause()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                videoListAdapter.onDestroy()
            }
        })
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> updateUI(state.data)
                    is UiState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun fetchVideoList() {
        viewModel.getVideoList()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
    }

    private fun updateUI(videoList: List<VideoItem>) {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        videoListAdapter.updateData(videoList)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
    }
}