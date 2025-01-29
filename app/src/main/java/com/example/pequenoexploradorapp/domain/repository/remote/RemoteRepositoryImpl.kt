package com.example.pequenoexploradorapp.domain.repository.remote

import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.NasaImageResponse
import com.example.pequenoexploradorapp.data.PictureOfTheDay
import com.example.pequenoexploradorapp.data.RoverImageResponse
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.domain.network.ApiResponse
import com.example.pequenoexploradorapp.domain.util.ConstantsApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException


class RemoteRepositoryImpl(private val client: HttpClient) : RemoteRepository {
    override suspend fun getRoverSpiritImages(date: String): ApiResponse<RoverImageResponse> =
        doRequest {
            client.get {
                url(BASE_URL_IMAGES_SPIRIT)
                parameter("earth_date", date)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverOpportunityImages(date: String): ApiResponse<RoverImageResponse> =
    doRequest {
        client.get {
            url(BASE_URL_IMAGES_OPPORTUNITY)
            parameter("earth_date", date)
            parameter("api_key", BuildConfig.API_KEY_DEMO)
        }
    }

    override suspend fun getRoverPerseveranceImages(date: String): ApiResponse<RoverImageResponse> =
        doRequest {
            client.get {
                url(BASE_URL_IMAGES_PERSEVERANCE)
                parameter("earth_date", date)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverCuriosityImages(date: String): ApiResponse<RoverImageResponse> =
        doRequest {
            client.get {
                url(BASE_URL_IMAGES_CURIOSITY)
                parameter("earth_date", date)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverSpiritMission(): ApiResponse<RoverMission> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_SPIRIT)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverOpportunityMission(): ApiResponse<RoverMission> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_OPPORTUNITY)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverPerseveranceMission(): ApiResponse<RoverMission> =
        doRequest {
            client.get {
                url(BASE_URL_ROVER_PERSEVERANCE)
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    override suspend fun getRoverCuriosityMission(): ApiResponse<RoverMission> =
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
                //parameter("date", "1995-06-16")
                parameter("api_key", BuildConfig.API_KEY_DEMO)
            }
        }

    private suspend inline fun <reified T> doRequest(crossinline request: suspend () -> HttpResponse): ApiResponse<T> {
        return try {
            val response: HttpResponse = request()
            when (response.status.value) {
                in 200..299 -> ApiResponse.success(data = response.body(), statusCode = response.status.value)
                in 300..399 -> throw RedirectResponseException(response.body(), response.status.description)
                in 400..499 -> throw ClientRequestException(response.body(), response.status.description)
                in 500..599 -> throw ServerResponseException(response.body(), response.status.description)
                else -> throw ResponseException(response.body(), response.status.description)
            }
        } catch (e: RedirectResponseException) {
            println("SERVER REDIRECTION => Status: ${e.response.status.value}, Error: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_SERVER
            )
        } catch (e: ClientRequestException) {
            println("SERVER CLIENT ERROR => Status: ${e.response.status.value}, Error: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_RESPONSE
            )
        } catch (e: ServerResponseException) {
            println("SERVER ERROR RESPONSE => Status: ${e.response.status.value}, Error: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_SERVER
            )
        } catch (e: ResponseException) {
            println("SERVER RESPONSE EXCEPTION => Status: ${e.response.status.value}, Error: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = e.response.status.value,
                messageError = ConstantsApp.ERROR_API
            )
        } catch (e: UnresolvedAddressException) {
            println("SERVER ERROR ADDRESS => Error: ${e.printStackTrace()}, Message: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_API
            )
        } catch (e: SerializationException) {
            println("ERROR SERIALIZATION => Error: ${e.printStackTrace()}, Message: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_SERIALIZATION
            )
        } catch (e: Exception) {
            println("EXCEPTION ERROR => Error: ${e.printStackTrace()}, Message: ${e.message}")
            ApiResponse.failure(
                exception = e,
                statusCode = null,
                messageError = ConstantsApp.ERROR_EXCEPTION
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
        private const val BASE_URL_IMAGES_SPIRIT = "https://api.nasa.gov/mars-photos/api/v1/rovers/spirit/photos"
        private const val BASE_URL_IMAGES_CURIOSITY = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos"
        private const val BASE_URL_IMAGES_OPPORTUNITY = "https://api.nasa.gov/mars-photos/api/v1/rovers/opportunity/photos"
        private const val BASE_URL_IMAGES_PERSEVERANCE = "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos"
    }
}
