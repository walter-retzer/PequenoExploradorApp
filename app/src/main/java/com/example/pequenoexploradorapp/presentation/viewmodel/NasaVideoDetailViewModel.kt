package com.example.pequenoexploradorapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import com.example.pequenoexploradorapp.domain.util.toHttpsPrefix
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException


class NasaVideoDetailViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val handle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_PLAYER_STATE = "player_state"
        private const val KEY_PLAYBACK_POSITION = "playback_position"
    }

    var playerState: Boolean
        get() = handle[KEY_PLAYER_STATE] ?: false
        set(value) = handle.set(KEY_PLAYER_STATE, value)

    private var playbackPosition: Long
        get() = handle[KEY_PLAYBACK_POSITION] ?: 0L
        set(value) = handle.set(KEY_PLAYBACK_POSITION, value)

    private val _uiState = MutableStateFlow<NasaVideoDetailViewState>(NasaVideoDetailViewState.Init)
    val uiState: StateFlow<NasaVideoDetailViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver.isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun savePlaybackPosition(player: ExoPlayer) {
        playbackPosition = player.currentPosition
    }

    fun restorePlaybackPosition(player: ExoPlayer) {
        player.seekTo(playbackPosition)
    }

    fun onVideoUrlToLoad(url: String) {
        viewModelScope.launch {
            delay(2000L)
            when (val response = remoteRepositoryImpl.fetchVideoUrl(url)) {
                is ApiResponse.Failure -> {
                    _uiState.value = NasaVideoDetailViewState.Error(response.messageError, true)
                }

                is ApiResponse.Success -> {
                    val videoUrl = getVideoUrl(createJsonArrayFromString(response.data))
                    _uiState.value = NasaVideoDetailViewState.Success(videoUrl.toString())
                }
            }
        }
    }

    private fun getVideoUrl(array: ArrayList<String>): String? {
        for (i in 0 until array.size) {
            val file = array[i].toHttpsPrefix()
            when {
                file.contains("mobile.mp4") -> { return file }
                file.contains(".mp4") -> { return file }
            }
        }
        return null
    }

    private fun createJsonArrayFromString(stringResponse: String): ArrayList<String> {
        val arrayList: ArrayList<String> = arrayListOf()
        try {
            val jsonArray = JSONArray(stringResponse)
            for (url in 0 until jsonArray.length()) {
                arrayList.add(jsonArray.getString(url))
            }
        } catch (e: JSONException) {
            Log.e("Error converting String response to a JSON Array", e.toString())
        }
        return arrayList
    }
}


sealed interface NasaVideoDetailViewState {
    data object Init : NasaVideoDetailViewState
    data class Success(val video: String) : NasaVideoDetailViewState
    data class Error(val message: String, val isActivated: Boolean) : NasaVideoDetailViewState
}
