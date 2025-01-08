package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverMissionCuriosity
import com.example.pequenoexploradorapp.data.RoverMissionOpportunity
import com.example.pequenoexploradorapp.data.RoverMissionPerseverance
import com.example.pequenoexploradorapp.data.RoverMissionSpirit
import com.example.pequenoexploradorapp.domain.network.ApiResponse

interface RemoteRepository {
    suspend fun getRoverSpiritMission(): ApiResponse<RoverMissionSpirit>

    suspend fun getRoverOpportunityMission(): ApiResponse<RoverMissionOpportunity>

    suspend fun getRoverPerseveranceMission(): ApiResponse<RoverMissionPerseverance>

    suspend fun getRoverCuriosityMission(): ApiResponse<RoverMissionCuriosity>


    suspend fun getNasaImage(
        imageSearch: String?,
        page: Int = 1,
        mediaType: String = "image"
    ): ApiResponse<NasaImageResponse>

    suspend fun getPictureOfTheDay(): ApiResponse<PictureOfTheDay>
}
