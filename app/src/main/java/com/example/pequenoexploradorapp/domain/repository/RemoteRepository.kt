package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ApiResponse

interface RemoteRepository {

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
