@file:Suppress("DEPRECATION")

package com.andy.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * 网络状态工具类，用于检查设备的网络连接状态。
 */
object NetworkUtil {

    /**
     * 判断当前设备是否连接到网络
     * @param context 上下文
     * @return true: 连接网络，false: 没有连接网络
     */
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.isConnected == true
        }
    }

    /**
     * 判断当前设备是否没有连接到网络
     */
    @JvmStatic
    fun isNetworkUnavailable(context: Context): Boolean {
        return !isNetworkAvailable(context)
    }

    /**
     * 判断当前设备是否连接到Wi-Fi
     */
    @JvmStatic
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.type == ConnectivityManager.TYPE_WIFI
        }
    }

    /**
     * 判断当前设备是否连接到移动数据网络
     */
    @JvmStatic
    fun isMobileConnected(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            activeNetwork?.type == ConnectivityManager.TYPE_MOBILE
        }
    }

    /**
     * 获取当前网络的类型（Wi-Fi 或 移动数据）
     * @return "WIFI"、"MOBILE" 或 "NONE"（如果没有网络连接）
     */
    @JvmStatic
    fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return "NONE"
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "NONE"
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "MOBILE"
                else -> "UNKNOWN"
            }
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            when (activeNetwork?.type) {
                ConnectivityManager.TYPE_WIFI -> "WIFI"
                ConnectivityManager.TYPE_MOBILE -> "MOBILE"
                else -> "NONE"
            }
        }
    }

    /**
     * 判断当前网络是否是Wi-Fi
     */
    @JvmStatic
    fun isConnectedToWifi(context: Context): Boolean {
        return isWifiConnected(context)
    }

    /**
     * 判断当前网络是否是移动数据
     */
    @JvmStatic
    fun isConnectedToMobile(context: Context): Boolean {
        return isMobileConnected(context)
    }
}