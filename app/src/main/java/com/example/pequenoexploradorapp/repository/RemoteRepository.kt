package com.example.pequenoexploradorapp.repository

import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.network.ResultNetwork

interface RemoteRepository {
    suspend fun getInfoRoversMission(): ResultNetwork<RoverMission>
}
