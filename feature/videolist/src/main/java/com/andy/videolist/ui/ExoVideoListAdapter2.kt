package com.andy.videolist.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.andy.common.gone
import com.andy.videolist.databinding.ItemExoVideoBinding
import com.andy.videolist.databinding.ItemVideoBinding
import com.andy.videolist.domain.model.VideoItem
import com.andy.videolist.utils.getAdaptiveVideoUrl
import java.io.File

/**
 * 视频列表适配器
 */
@OptIn(UnstableApi::class)
class ExoVideoListAdapter2(
    private val context: Context
) : RecyclerView.Adapter<ExoVideoListAdapter2.VideoViewHolder>(), SimpleLifeCycle {
    private val items: MutableList<VideoItem> = mutableListOf()

    private lateinit var player: ExoPlayer
    private var simpleCache: SimpleCache

    init {
        val evict = LeastRecentlyUsedCacheEvictor((100 * 1024 * 1024).toLong())
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
        simpleCache = SimpleCache(File(context.cacheDir, "media"), evict, databaseProvider)
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
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                holder.binding.playerView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(Uri.parse(item.videoUrl))
                val httpDataSourceFactory =
                    DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                val defaultDataSourceFactory =
                    DefaultDataSourceFactory(context, httpDataSourceFactory)
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(simpleCache)
                    .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                val mediaSource = HlsMediaSource
                    .Factory(cacheDataSourceFactory)
                    .createMediaSource(mediaItem)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.playWhenReady = false
                exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                exoPlayer.prepare()
            }
    }

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d("ViewPager2", "View 加入屏幕 第${holder.adapterPosition + 1}页")
        val videoItem = items[holder.adapterPosition]
        val context = holder.binding.root.context
        val videoUrl = videoItem.getAdaptiveVideoUrl(context)
        Log.d("ViewPager2", "View 加入屏幕 第${holder.adapterPosition + 1}页, videoUrl = $videoUrl")
        holder.binding.playerView.player?.apply {
            seekTo(0)
            playWhenReady = true
            if (this.playerError != null) {
                prepare()
            }
            addListener(object : Listener {
                override fun onRenderedFirstFrame() {
                    super.onRenderedFirstFrame()
                    holder.binding.coverImage.gone()
                }
            })
        }
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("ViewPager2", "View 离屏 第${holder.adapterPosition + 1}页")
        holder.binding.playerView.player?.pause()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d("Yt Shorts", "Player Released onDetach")
        player.release()
        simpleCache.release()
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        Log.d("YT Shorts", "View recycled")
        holder.binding.playerView.player?.release()
    }

    override fun getItemCount(): Int = items.size

    fun onPageSelected(position: Int) {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }
}
