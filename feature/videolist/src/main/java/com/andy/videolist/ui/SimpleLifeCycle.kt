package com.andy.videolist.ui

/**
 * SimpleLifeCycle 是一个简单的生命周期接口，用于管理组件的生命周期。
 */
interface SimpleLifeCycle {
    fun onResume()
    fun onPause()
    fun onDestroy()
}