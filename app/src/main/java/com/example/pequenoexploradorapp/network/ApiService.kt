package com.example.pequenoexploradorapp.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL_IMAGES = "https://images-api.nasa.gov/"
        private const val BASE_URL_ROVERS = "https://api.nasa.gov/mars-photos/api/v1/rovers/"
    }

    suspend fun getRoversMission() = client.get(BASE_URL_ROVERS)
}
