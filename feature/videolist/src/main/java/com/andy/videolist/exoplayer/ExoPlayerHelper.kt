package com.andy.videolist.exoplayer

import android.util.Log
import android.util.SparseArray
import androidx.annotation.OptIn
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.media3.common.util.UnstableApi
import com.andy.common.gone
import com.andy.common.visible
import com.andy.videolist.ui.ExoVideoListAdapter
import com.andy.videolist.ui.SimpleLifeCycle

/**
 * ExoPlayerHelper 负责管理多个视频播放器的生命周期和状态。
 * 它支持视频播放/暂停切换、播放器创建与销毁、以及播放器的状态更新。
 * 该类的核心功能是确保播放器在 RecyclerView 中滚动时正常播放和暂停，
 * 并在视图离屏时释放资源。
 */
class ExoPlayerHelper : SimpleLifeCycle {
    /**
     * 存储当前播放器在RecyclerView中的位置和播放时间
     */
    private val positionToPlaybackTime = SparseArray<Long>()

    /**
     * 缓存当前Attach到RecyclerView的ViewHolder和播放器，最多存储3个
     */
    private val playerWrappers: MutableList<ExoPlayerWrapper> = mutableListOf()
    private var currentPosition = 0

    /**
     * 切换指定播放器的播放/暂停状态。
     * 如果播放器正在播放，则暂停并显示暂停按钮；
     * 如果播放器已暂停，则恢复播放并隐藏暂停按钮。
     *
     * @param holder 需要切换播放状态的 VideoViewHolder
     */
    fun togglePlayerPlayback(holder: ExoVideoListAdapter.VideoViewHolder) {
        getPlayerWrapper(holder.bindingAdapterPosition)?.let {
            if (it.player.isPlaying) {
                it.player.pause()
                it.isManualPaused = true
                holder.binding.imagePause.visible()
            } else {
                it.player.play()
                it.isManualPaused = false
                holder.binding.imagePause.gone()
            }
        }
    }

    /**
     * 创建一个新的播放器包装对象，并将其添加到 playerWrappers 列表中。
     *
     * @param videoViewHolder 当前视频项的 ViewHolder，用于关联播放器和视图
     * @param videoUrl 播放的视频 url
     */
    @OptIn(UnstableApi::class)
    fun createAndAddPlayerWrapper(
        videoViewHolder: ExoVideoListAdapter.VideoViewHolder,
        videoUrl: String
    ) {
        val context = videoViewHolder.binding.root.context

        ExoPlayerWrapper(
            player = ExoPlayerSupplier.initializeExoPlayer(
                context = context,
                videoUrl = videoUrl,
                onPlaying = {
                    videoViewHolder.binding.imagePause.gone()
                    videoViewHolder.binding.coverImage.gone()
                }
            ),
            holder = videoViewHolder
        ).apply {
            val currentPlaybackTime =
                positionToPlaybackTime.get(videoViewHolder.bindingAdapterPosition, 0L)
            if (currentPlaybackTime > 0) {
                player.seekTo(currentPlaybackTime)
            }
            player.playWhenReady = false
        }.also {
            playerWrappers.add(it)
        }
    }

    /**
     * 暂停并从 playerWrappers 列表中移除指定位置的播放器对象。
     * 该方法用于处理视图离屏时，暂停播放器并清理相关资源。
     *
     * @param videoViewHolder 当前视频项的 ViewHolder，表示要操作的播放器所在的视图
     */
    fun pauseAndRemoveOffscreenPlayer(videoViewHolder: ExoVideoListAdapter.VideoViewHolder) {
        videoViewHolder.binding.playerView.player?.pause()

        val iterator = playerWrappers.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.holder.bindingAdapterPosition == videoViewHolder.bindingAdapterPosition) {
                item.holder.binding.playerView.player = null
                item.player.stop()
                item.player.release()
                iterator.remove()

                Log.d(
                    "ViewPager2", "第${item.holder.bindingAdapterPosition + 1}个" +
                            "离屏, playerWrappers.size : ${playerWrappers.size}, " +
                            "videoViewHolder.binding.playerView.player: ${videoViewHolder.binding.playerView.player}, " +
                            "item.holder.binding.playerView.player: ${item.holder.binding.playerView.player}"
                )
                break
            }
        }
    }

    /**
     * 更新播放器的状态，当页面被选中时恢复播放，其他页面暂停播放。
     *
     * @param position 当前选中的页面位置
     */
    fun updatePlayerForSelectedPage(position: Int) {
        val lastPosition = currentPosition
        currentPosition = position

        // 找到前一个播放器，暂停并记录当前播放时间
        playerWrappers.find {
            it.holder.bindingAdapterPosition == lastPosition
                    && lastPosition != currentPosition
        }?.let {
            Log.d(
                "ViewPager2", "第${it.holder.bindingAdapterPosition + 1}" +
                        "页播放暂停, 当前进度: ${it.player.currentPosition}"
            )
            // 记录当前播放器在RecyclerView Adapter中的位置和播放时间
            positionToPlaybackTime.put(
                it.holder.bindingAdapterPosition,
                it.player.currentPosition
            )
            it.player.playWhenReady = false
        }

        // 找到当前播放器，恢复播放
        playerWrappers.find {
            it.holder.bindingAdapterPosition == position
        }?.let {
            val currentPlaybackTime = positionToPlaybackTime.get(position, 0L)
            it.holder.binding.playerView.player = it.player
            if (currentPlaybackTime > 0) {
                it.player.seekTo(currentPlaybackTime)
            }
            it.player.playWhenReady = true

            Log.d(
                "ViewPager2", "当前屏幕是第${position + 1}页, " +
                        "播放进度: $currentPlaybackTime, it.player.isPlaying: " +
                        "${it.player.isPlaying}"
            )
        }
    }

    override fun onResume() {
        getCurrentPlayerWrapper()?.let {
            // 如果不是用户手动暂停，需要恢复播放
            if (!it.isManualPaused) {
                it.player.play()
            }
        }
    }

    override fun onPause() {
        getCurrentPlayerWrapper()?.player?.pause()
    }

    override fun onDestroy() {
        playerWrappers.forEach {
            it.player.release()
        }
        playerWrappers.clear()
    }

    private fun getCurrentPlayerWrapper(): ExoPlayerWrapper? {
        return getPlayerWrapper(currentPosition)
    }

    private fun getPlayerWrapper(position: Int): ExoPlayerWrapper? {
        return playerWrappers.find {
            it.holder.bindingAdapterPosition == position
        }
    }
}