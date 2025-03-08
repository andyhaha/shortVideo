package com.andy.videolist.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.andy.common.visible
import com.andy.videolist.databinding.ItemVideoBinding
import com.andy.videolist.domain.model.VideoItem
import com.andy.videolist.utils.getAdaptiveVideoUrl

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>(), SimpleLifeCycle {
    private val items: MutableList<VideoItem> = mutableListOf()
    private val playerHelper by lazy(LazyThreadSafetyMode.NONE) {
        TXVodPlayerHelper()
    }

    fun updateData(list: List<VideoItem>) {
        items.clear()
        items.addAll(list)
    }

    inner class VideoViewHolder(
        val binding: ItemVideoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                playerHelper.togglePlayerPlayback(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = items[position]
        holder.binding.coverImage.load(item.coverUrl) {
            placeholder(android.R.color.black)
            error(android.R.color.black)
        }
        // TODO 调试使用，标记当前是第几个
        holder.binding.textPosition.apply {
            visible()
            text = "第 ${position + 1} 个"
        }
    }

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
//        Log.d("ViewPager2", "View 加入屏幕 第${holder.adapterPosition + 1}页")
        val videoItem = items[holder.adapterPosition]
        val context = holder.binding.root.context
        val videoUrl = videoItem.getAdaptiveVideoUrl(context)
//        Log.d("ViewPager2", "View 加入屏幕 第${holder.adapterPosition + 1}页, videoUrl = $videoUrl")

        playerHelper.createAndAddPlayerWrapper(
            videoViewHolder = holder,
            videoUrl = videoUrl
        )
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
//        Log.d("ViewPager2", "View 离屏 第${holder.adapterPosition + 1}页")
        playerHelper.pauseAndRemoveOffscreenPlayer(holder)
    }

    override fun getItemCount(): Int = items.size

    fun onPageSelected(position: Int) {
        playerHelper.updatePlayerForSelectedPage(position)
    }

    override fun onResume() {
        playerHelper.onResume()
    }

    override fun onPause() {
        playerHelper.onPause()
    }

    override fun onDestroy() {
        playerHelper.onDestroy()
    }
}
