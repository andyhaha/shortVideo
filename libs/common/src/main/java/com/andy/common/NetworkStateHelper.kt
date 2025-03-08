package com.andy.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import java.lang.ref.WeakReference

/**
 * NetworkStateHelper 用于管理网络状态变化。
 * 该类使用 ConnectivityManager 来注册网络回调，监控网络可用性、网络类型（Wi-Fi 或移动数据）以及网络能力的变化。
 * 它允许动态注册和注销监听器，为处理应用中的网络状态变化提供灵活性。
 */
class NetworkStateHelper(context: Context) {

    /**
     * 网络状态监听器接口。
     * 实现此接口可以接收网络可用性、网络类型变化和网络不可用的更新。
     */
    interface NetworkStateListener {
        fun onNetworkAvailable() {}
        fun onNetworkUnavailable() {}
        fun onNetworkTypeChanged(isWifi: Boolean) {}
    }

    // 存储网络状态监听器
    private var listener: WeakReference<NetworkStateListener>? = null

    // 用于注册网络状态变化的 ConnectivityManager
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkRequest = NetworkRequest.Builder().build()

    // 网络回调实例
    private val networkCallback = NetworkCallbackWrapper()

    init {
        // 在初始化时注册网络回调
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * 注册网络状态变化的监听器
     * @param listener 需要接收网络状态变化的监听器
     */
    fun registerNetworkCallback(listener: NetworkStateListener) {
        // 使用 WeakReference 避免内存泄漏
        this.listener = WeakReference(listener)
    }

    /**
     * 注销当前网络状态变化监听器
     */
    fun unregisterNetworkCallback() {
        listener?.clear()
        listener = null
        Log.d("NetworkState", "已注销网络回调")
    }

    /**
     * 内部类，包装每个监听器对应的 NetworkCallback。
     * 该类允许为每个监听器单独注册和注销网络回调。
     */
    private inner class NetworkCallbackWrapper : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d("NetworkState", "网络可用")
            listener?.get()?.onNetworkAvailable()
        }

        override fun onLost(network: Network) {
            Log.d("NetworkState", "网络丢失")
            listener?.get()?.onNetworkUnavailable()
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            Log.d("NetworkState", if (isWifi) "连接到 Wi-Fi" else "连接到移动网络")
            listener?.get()?.onNetworkTypeChanged(isWifi)
        }

        override fun onUnavailable() {
            Log.d("NetworkState", "网络不可用")
            listener?.get()?.onNetworkUnavailable()
        }
    }
}