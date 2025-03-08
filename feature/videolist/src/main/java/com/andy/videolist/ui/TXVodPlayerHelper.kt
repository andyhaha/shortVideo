package com.andy.videolist.ui

import android.util.Log
import android.util.SparseArray
import com.andy.common.gone
import com.andy.common.visible
import com.andy.videolist.txplayer.TXVodPlayerSupplier

/**
 * TXVodPlayerHelper 负责管理多个视频播放器的生命周期和状态。
 * 它支持视频播放/暂停切换、播放器创建与销毁、以及播放器的状态更新。
 * 该类的核心功能是确保播放器在 RecyclerView 中滚动时正常播放和暂停，
 * 并在视图离屏时释放资源。
 */
class TXVodPlayerHelper : SimpleLifeCycle {

    /**
     * 存储当前播放器在RecyclerView中的位置和播放时间
     */
    private val positionToPlaybackTime = SparseArray<Float>()

    /**
     * 缓存当前Attach到RecyclerView的ViewHolder和播放器，最多存储3个
     */
    private val playerWrappers: MutableList<TXVodPlayerWrapper> = mutableListOf()
    private var currentPosition = 0

    /**
     * 切换指定播放器的播放/暂停状态。
     * 如果播放器正在播放，则暂停并显示暂停按钮；
     * 如果播放器已暂停，则恢复播放并隐藏暂停按钮。
     *
     * @param holder 需要切换播放状态的 VideoViewHolder
     */
    fun togglePlayerPlayback(holder: VideoListAdapter.VideoViewHolder) {
        getPlayerWrapper(holder.adapterPosition)?.let {
            if (it.player.isPlaying) {
                it.player.pause()
                it.isManualPaused = true
                holder.binding.imagePause.visible()
            } else {
                it.player.resume()
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
    fun createAndAddPlayerWrapper(
        videoViewHolder: VideoListAdapter.VideoViewHolder,
        videoUrl: String
    ) {
        val context = videoViewHolder.binding.root.context
        TXVodPlayerWrapper(
            player = TXVodPlayerSupplier.createPlayer(
                context = context,
                onPlayStart = {
                    videoViewHolder.binding.progressBar.gone()
                    videoViewHolder.binding.imagePause.gone()
                }
            ),
            holder = videoViewHolder
        ).apply {
            // setAutoPlay 设置为false，不会立刻开始播放，而只会开始加载视频
            player.setAutoPlay(false)
            player.startVodPlay(videoUrl)
            val currentPlaybackTime =
                positionToPlaybackTime.get(videoViewHolder.adapterPosition, 0f)
            player.seek(currentPlaybackTime, true)
            player.pause()
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
    fun pauseAndRemoveOffscreenPlayer(videoViewHolder: VideoListAdapter.VideoViewHolder) {
        val iterator = playerWrappers.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.holder.adapterPosition == videoViewHolder.adapterPosition) {
                // 移除的时候务必调用stopPlay方法释放资源
                item.player.setVodListener(null)
                item.player.stopPlay(true)
                iterator.remove()
                Log.d(
                    "ViewPager2", "第${item.holder.adapterPosition}个" +
                            "离屏, playerWrappers.size : ${playerWrappers.size}"
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
            it.holder.adapterPosition == lastPosition
        }?.let {
            Log.d(
                "ViewPager2", "第${it.holder.adapterPosition + 1}" +
                        "页播放暂停, 当前进度: ${it.player.currentPlaybackTime}"
            )
            // 记录当前播放器在RecyclerView Adapter中的位置和播放时间
            positionToPlaybackTime.put(
                it.holder.adapterPosition,
                it.player.currentPlaybackTime
            )
            it.player.pause()
        }

        // 找到当前播放器，恢复播放
        playerWrappers.find {
            it.holder.adapterPosition == position
        }?.let {
            val currentPlaybackTime = positionToPlaybackTime.get(position, 0f)
            it.player.setPlayerView(it.holder.binding.videoView)
            it.player.seek(currentPlaybackTime, true)
            it.player.resume()
            Log.d(
                "ViewPager2", "当前屏幕是第${position + 1}页, " +
                        "播放进度: $currentPlaybackTime"
            )
            if (it.player.isPlaying) {
                Log.d("ViewPager2", "第${position + 1}页播放11111111")
                it.holder.binding.imagePause.gone()
                it.holder.binding.progressBar.gone()
            }
        }
    }

    override fun onResume() {
        getCurrentPlayerWrapper()?.let {
            // 如果不是用户手动暂停，需要恢复播放
            if (!it.isManualPaused) {
                it.player.resume()
            }
        }
    }

    override fun onPause() {
        getCurrentPlayerWrapper()?.player?.pause()
    }

    override fun onDestroy() {
        playerWrappers.forEach {
            it.player.stopPlay(true)
        }
        playerWrappers.clear()
    }

    private fun getCurrentPlayerWrapper(): TXVodPlayerWrapper? {
        return getPlayerWrapper(currentPosition)
    }

    private fun getPlayerWrapper(position: Int): TXVodPlayerWrapper? {
        return playerWrappers.find {
            it.holder.adapterPosition == position
        }
    }
}