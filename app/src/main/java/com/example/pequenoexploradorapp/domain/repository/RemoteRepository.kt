package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ResultNetwork

interface RemoteRepository {
    suspend fun getInfoRoversMission(): ResultNetwork<RoverMission>
}
