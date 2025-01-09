package com.example.pequenoexploradorapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoverMission(
    @SerialName("rover") val rover: Rover
)

@Serializable
data class Rover(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("landing_date") val landingDate: String,
    @SerialName("launch_date") val launchDate: String,
    @SerialName("status") val status: String,
    @SerialName("max_sol") val maxSun: String,
    @SerialName("max_date") val maxDate: String,
    @SerialName("total_photos") val totalPhotos: Int,
    @SerialName("cameras") val cameras: List<RoverCameraType>
)

@Serializable
data class RoverCameraType(
    @SerialName("name") val name: String,
    @SerialName("full_name") val fullName: String
)
