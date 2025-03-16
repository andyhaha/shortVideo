package com.andy.videolist.ui

/**
 * UiState 是一个密封类，用于表示界面状态。
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
