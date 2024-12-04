package com.example.pequenoexploradorapp.repository

import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.network.ApiService
import io.ktor.client.call.body


class RemoteRepositoryImpl(private val apiService: ApiService) : RemoteRepository {
    override suspend fun getInfoRoversMission(): Result<RoverMission> =
        runCatching {
            apiService.getRoversMission().body<RoverMission>()
        }
}
