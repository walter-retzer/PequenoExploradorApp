package com.example.pequenoexploradorapp.repository

import com.example.pequenoexploradorapp.data.RoverMission

interface RemoteRepository {
    suspend fun getInfoRoversMission(): Result<RoverMission>
}