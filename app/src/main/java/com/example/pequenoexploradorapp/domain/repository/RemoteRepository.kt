package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ApiResponse

interface RemoteRepository {
    suspend fun getInfoRoversMission(): ApiResponse<RoverMission>
}
