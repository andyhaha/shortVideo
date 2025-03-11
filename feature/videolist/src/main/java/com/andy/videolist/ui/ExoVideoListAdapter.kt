package com.andy.videolist.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.andy.videolist.databinding.ItemExoVideoBinding
import com.andy.videolist.databinding.ItemVideoBinding
import com.andy.videolist.domain.model.VideoItem
import com.andy.videolist.utils.getAdaptiveVideoUrl

/**
 * 视频列表适配器
 */
class ExoVideoListAdapter : RecyclerView.Adapter<ExoVideoListAdapter.VideoViewHolder>(), SimpleLifeCycle {
    private val items: MutableList<VideoItem> = mutableListOf()

    /**
     * 视频帮助类
     */
    private val playerHelper by lazy(LazyThreadSafetyMode.NONE) {
        ExoPlayerHelper()
    }

    fun updateData(list: List<VideoItem>) {
        items.clear()
        items.addAll(list)
    }

    inner class VideoViewHolder(
        val binding: ItemExoVideoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                playerHelper.togglePlayerPlayback(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemExoVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = items[position]
        holder.binding.coverImage.load(item.coverUrl) {
            placeholder(android.R.color.black)
            error(android.R.color.black)
        }
        Log.d("ViewPager2", "onBindViewHolder() 第${position + 1}页")
    }

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d("ViewPager2", "View 加入屏幕 第${holder.bindingAdapterPosition + 1}页")
        val videoItem = items[holder.bindingAdapterPosition]
        val context = holder.binding.root.context
        val videoUrl = videoItem.getAdaptiveVideoUrl(context)
        Log.d("ViewPager2", "View 加入屏幕 第${holder.bindingAdapterPosition + 1}页, videoUrl = $videoUrl")

        playerHelper.createAndAddPlayerWrapper(
            videoViewHolder = holder,
            videoUrl = videoUrl
        )
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("ViewPager2", "View 离屏 第${holder.bindingAdapterPosition + 1}页")
        playerHelper.pauseAndRemoveOffscreenPlayer(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.d("ViewPager2", "onAttachedToRecyclerView()")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("ViewPager2", "onAttachedToRecyclerView()")
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.playerView.player?.release()
        Log.d("ViewPager2", "onViewRecycled() 第${holder.bindingAdapterPosition + 1}页")
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
