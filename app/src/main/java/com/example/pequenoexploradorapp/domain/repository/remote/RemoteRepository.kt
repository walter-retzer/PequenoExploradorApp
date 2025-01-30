package com.example.pequenoexploradorapp.domain.repository.remote

import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverImageResponse
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ApiResponse

interface RemoteRepository {

    suspend fun getNasaVideos(
        imageSearch: String?,
        page: Int = 1,
        mediaType: String = "video"
    ): ApiResponse<NasaImageResponse>

    suspend fun fetchVideoUrl(url: String): ApiResponse<String>

    suspend fun getRoverSpiritImages(date: String): ApiResponse<RoverImageResponse>

    suspend fun getRoverOpportunityImages(date: String): ApiResponse<RoverImageResponse>

    suspend fun getRoverPerseveranceImages(date: String): ApiResponse<RoverImageResponse>

    suspend fun getRoverCuriosityImages(date: String): ApiResponse<RoverImageResponse>

    suspend fun getRoverSpiritMission(): ApiResponse<RoverMission>

    suspend fun getRoverOpportunityMission(): ApiResponse<RoverMission>

    suspend fun getRoverPerseveranceMission(): ApiResponse<RoverMission>

    suspend fun getRoverCuriosityMission(): ApiResponse<RoverMission>


    suspend fun getNasaImage(
        imageSearch: String?,
        page: Int = 1,
        mediaType: String = "image"
    ): ApiResponse<NasaImageResponse>

    suspend fun getPictureOfTheDay(): ApiResponse<PictureOfTheDay>
}
