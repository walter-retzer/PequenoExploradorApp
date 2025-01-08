package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverMissionCuriosity
import com.example.pequenoexploradorapp.data.RoverMissionOpportunity
import com.example.pequenoexploradorapp.data.RoverMissionPerseverance
import com.example.pequenoexploradorapp.data.RoverMissionSpirit
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException


class RemoteRepositoryImpl(private val client: HttpClient) : RemoteRepository {

    override suspend fun getRoverSpiritMission(): ApiResponse<RoverMissionSpirit> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_SPIRIT)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverOpportunityMission(): ApiResponse<RoverMissionOpportunity> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_OPPORTUNITY)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverPerseveranceMission(): ApiResponse<RoverMissionPerseverance> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_PERSEVERANCE)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverCuriosityMission(): ApiResponse<RoverMissionCuriosity> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_CURIOSITY)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getNasaImage(
        imageSearch: String?,
        page: Int,
        mediaType: String
    ): ApiResponse<NasaImageResponse> =
        doRequest {
            client.get {
                url(BASE_URL_IMAGES)
                parameter("q", imageSearch)
                parameter("page", page)
                parameter("media_type", mediaType)
            }
        }

    override suspend fun getPictureOfTheDay(): ApiResponse<PictureOfTheDay>  =
        doRequest {
            client.get {
                url(BASE_URL_PICTURE_OF_THE_DAY)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    private suspend inline fun <reified T> doRequest(crossinline request: suspend () -> HttpResponse): ApiResponse<T> {
        return try {
            val response: HttpResponse = request()
            ApiResponse.success(data = response.body(), statusCode = response.status.value)
        } catch (e: RedirectResponseException) {
            // 3xx - response
            println("Error: ${e.response.status.description}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_SERVER
            )
        } catch (e: ClientRequestException) {
            // 4xx - response
            println("Error: ${e.response.status.description}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_API
            )
        } catch (e: ServerResponseException) {
            // 5xx - response
            println("Error: ${e.response.status.description}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_SERVER
            )
        } catch (e: UnresolvedAddressException) {
            println("Error: ${e.printStackTrace()}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_SERVER
            )
        } catch (e: SerializationException) {
            println("Error: ${e.printStackTrace()}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_API
            )
        } catch (e: Exception) {
            println("Error: ${e.printStackTrace()}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_API
            )
        }
    }

    companion object {
        private const val BASE_URL_IMAGES = "https://images-api.nasa.gov/search"
        private const val BASE_URL_ROVER_SPIRIT = "https://api.nasa.gov/mars-photos/api/v1/rovers/spirit/"
        private const val BASE_URL_ROVER_CURIOSITY = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/"
        private const val BASE_URL_ROVER_OPPORTUNITY = "https://api.nasa.gov/mars-photos/api/v1/rovers/opportunity/"
        private const val BASE_URL_ROVER_PERSEVERANCE = "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/"
        private const val BASE_URL_PICTURE_OF_THE_DAY = "https://api.nasa.gov/planetary/apod"
    }
}
