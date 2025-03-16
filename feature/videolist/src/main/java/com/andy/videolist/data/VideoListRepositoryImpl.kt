package com.andy.videolist.data


import android.content.Context
import com.andy.videolist.data.model.VideoListBean
import com.andy.videolist.domain.model.VideoItem
import com.andy.videolist.domain.repository.VideoListRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class VideoListRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : VideoListRepository {
    companion object {
        /**
         * assets目录下mock数据的文件名
         */
        private const val JSON_FILE_NAME = "video_list_mock.json"
    }

    private val moshi: Moshi = Moshi.Builder()
        .build()
    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter = moshi.adapter<VideoListBean>()

    override fun getVideoList(): Flow<List<VideoItem>> = flow {
        val jsonString = readJsonFromAssets()
        jsonAdapter.fromJson(jsonString)?.also {
            emit(it.data)
        }
    }

    private fun readJsonFromAssets(): String {
        val inputStream = context.assets.open(JSON_FILE_NAME)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.use { it.readText() }
    }
}
