@file:Suppress("DEPRECATION")

package com.andy.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * 网络状态工具类，用于检查设备的网络连接状态。
 * 提供方法来判断是否连接网络、当前是否连接 Wi-Fi、以及当前是否为移动数据网络等。
 */
object NetworkUtil {

    /**
     * 判断当前设备是否连接到网络
     * @param context 上下文
     * @return true: 连接网络，false: 没有连接网络
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    /**
     * 判断当前设备是否没有连接到网络
     * @param context 上下文
     * @return true: 没有连接网络，false: 连接网络
     */
    fun isNetworkUnavailable(context: Context): Boolean {
        return !isNetworkAvailable(context)
    }

    /**
     * 判断当前设备是否连接到Wi-Fi
     * @param context 上下文
     * @return true: 当前连接 Wi-Fi，false: 当前连接非 Wi-Fi 网络
     */
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 判断当前设备是否连接到移动数据网络
     * @param context 上下文
     * @return true: 当前连接 移动数据网络，false: 当前连接其他类型的网络
     */
    fun isMobileDataConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.type == ConnectivityManager.TYPE_MOBILE
    }

    /**
     * 获取当前网络的类型（Wi-Fi 或 移动数据）
     * @param context 上下文
     * @return "WIFI" 或 "MOBILE" 或 "NONE"（如果没有网络连接）
     */
    fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return when {
            activeNetwork == null -> "NONE" // 没有网络连接
            activeNetwork.type == ConnectivityManager.TYPE_WIFI -> "WIFI"
            activeNetwork.type == ConnectivityManager.TYPE_MOBILE -> "MOBILE"
            else -> "UNKNOWN"
        }
    }

    /**
     * 判断当前网络是否是Wi-Fi
     * @param context 上下文
     * @return true: 网络是 Wi-Fi，false: 其他类型
     */
    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    /**
     * 判断当前网络是否是移动数据
     * @param context 上下文
     * @return true: 网络是 移动数据，false: 其他类型
     */
    fun isConnectedToMobileData(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    }
}
