package com.example.pequenoexploradorapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RoverImageResponse(
    @SerialName("photos") val photos: List<RoverImageInfo>
)

@Serializable
data class RoverImageInfo(
    @SerialName("id") val id: String,
    @SerialName("sol") val sol: Int,
    @SerialName("camera") val camera: RoverCamera,
    @SerialName("img_src") val image: String,
    @SerialName("earth_date") val date: String,
    @SerialName("rover") val rover: RoverInfo
)

@Serializable
data class RoverCamera(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("rover_id") val roverId: Int,
    @SerialName("full_name") val fullName: String
)

@Serializable
data class RoverInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("landing_date") val landingDate: String,
    @SerialName("launch_date") val launchDate: String,
    @SerialName("status") val status: String
)
