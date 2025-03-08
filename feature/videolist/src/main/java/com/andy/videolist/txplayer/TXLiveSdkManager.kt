package com.andy.videolist.txplayer

import android.content.Context
import android.util.Log
import com.tencent.rtmp.TXLiveBase
import com.tencent.rtmp.TXLiveBaseListener
import com.tencent.rtmp.TXPlayerGlobalSetting
import java.io.File


object TXLiveSdkManager {
    private const val TAG = "TxPlayerSdkManager"
    /**
     * 获取到的 license url
     */
    private const val LICENSE_URL = "https://license.vod2.myqcloud.com/license/v2/1346974751_1/v_cube.license"

    /**
     * 获取到的 license key
     */
    private const val LICENSE_KEY = "26bcbe3150d1d87f1b3f5dfaf46c71d0"

    private const val VIDEO_CACHE_PATH = "/videos"

    fun init(context: Context) {
        TXLiveBase.getInstance().setLicence(context, LICENSE_URL, LICENSE_KEY)
        TXLiveBase.setListener(object : TXLiveBaseListener() {
            override fun onLicenceLoaded(result: Int, reason: String) {
                Log.i(TAG, "onLicenceLoaded: result:$result, reason:$reason")
            }
        })
        val sdcardDir: File? = context.getExternalFilesDir(null)
        if (sdcardDir != null) {
            //设置播放引擎的全局缓存目录
            TXPlayerGlobalSetting.setCacheFolderPath(sdcardDir.path + VIDEO_CACHE_PATH)
            //设置播放引擎的全局缓存目录和缓存大小，//单位MB
            TXPlayerGlobalSetting.setMaxCacheSize(200)
        }
    }
}