package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ApiResponse

interface RemoteRepository {
    suspend fun getInfoRoversMission(): ApiResponse<RoverMission>

    suspend fun getNasaImage(
        imageSearch: String,
        page: Int = 1,
        mediaType: String = "image"
    ): ApiResponse<NasaImageResponse>
}
