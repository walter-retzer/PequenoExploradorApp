package com.example.pequenoexploradorapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.data.NasaImageItems
import com.example.pequenoexploradorapp.domain.connectivity.ConnectivityObserver
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.repository.local.FavouriteImageRepositoryImpl
import com.example.pequenoexploradorapp.domain.repository.remote.RemoteRepositoryImpl
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException


class NasaVideoDetailViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val remoteRepositoryImpl: RemoteRepositoryImpl,
    private val localRepositoryImpl: FavouriteImageRepositoryImpl,
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

    private var video = ""
    private var page = 1
    private var totalHits = 0

    private var listOfVideosFromApi = emptyList<NasaImageItems>()
    private val _listOfVideosFromApi = MutableStateFlow(listOfVideosFromApi)

    private val _uiState = MutableStateFlow<LoadNasaVideoViewState>(LoadNasaVideoViewState.Init)
    val uiState: StateFlow<LoadNasaVideoViewState> = _uiState.asStateFlow()

    val isConnected = connectivityObserver
        .isConnected
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

    suspend fun onVideoUrlToLoad(url: String): String? {
        return withContext(Dispatchers.IO) {
            when (val response = remoteRepositoryImpl.fetchVideoUrl(url)) {
                is ApiResponse.Failure -> null
                is ApiResponse.Success -> getVideoUrl(createJsonArrayFromString(response.data))
            }
        }
    }

    private fun getVideoUrl(array: ArrayList<String>): String? {
        Log.d("getVideoUrl", array.toString())
        for (i in 0 until array.size) {
            val file = array[i].replace("http://", "https://")
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
    data class Loading(
        val isLoading: Boolean,
        val listOfNasaImage: List<NasaImageItems>,
        val totalHits: Int
    ) : NasaVideoDetailViewState

    data class SuccessAddFavourite(
        val updateListOfImageFavourite: List<NasaImageItems>,
        val totalHits: Int
    ) : NasaVideoDetailViewState

    data class SuccessLoadMoreVideos(
        val updateListOfVideos: List<NasaImageItems>,
        val totalHits: Int
    ) : NasaVideoDetailViewState

    data class SuccessVideo(
        val video: List<NasaImageItems>,
        val totalHits: Int
    ) : NasaVideoDetailViewState

    data class Error(
        val message: String,
        val isActivated: Boolean
    ) : NasaVideoDetailViewState
}
