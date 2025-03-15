package com.andy.videolist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andy.videolist.domain.model.VideoItem
import com.andy.videolist.domain.repository.VideoListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<VideoItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<VideoItem>>> get() = _uiState

    fun getVideoList() {
        viewModelScope.launch {
            try {
                repository.getVideoList().collect { videoList ->
                    _uiState.value = UiState.Success(videoList)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load data: ${e.message}")
            }
        }
    }
}