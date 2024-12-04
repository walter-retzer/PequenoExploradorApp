package com.example.pequenoexploradorapp.repository

import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.RoverMission
import com.example.pequenoexploradorapp.network.ApiService
import com.example.pequenoexploradorapp.network.ResultNetwork
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


class RemoteRepositoryImpl(private val apiService: ApiService, private val client: HttpClient) : RemoteRepository {


    private val BASE_URL_ROVERS = "https://api.nasa.gov/mars-photos/api/v1/rovers/"

    override suspend fun getInfoRoversMission(): Result<RoverMission> =
        runCatching {
            apiService.getRoversMission().body<RoverMission>()
        }

    suspend fun getValues(): ResultNetwork<RoverMission> =
        makeRequest {
            client.get{
                url(BASE_URL_ROVERS)
                parameter("api_key", BuildConfig.API_KEY)
            }
        }

    suspend inline fun <reified T> makeRequest(crossinline request: suspend () -> HttpResponse): ResultNetwork<T> {
        return try {
            val response: HttpResponse = request()
            ResultNetwork.success(data = response.body(), statusCode = response.status.value)
        } catch (e: RedirectResponseException) {
            // 3xx - response
            println("Error: ${e.response.status.description}")
            ResultNetwork.failure(exception = e, statusCode = e.response.status.value)
        } catch (e: ClientRequestException) {
            // 4xx - response
            println("Error: ${e.response.status.description}")
            ResultNetwork.failure(exception = e, statusCode = e.response.status.value)
        } catch (e: ServerResponseException) {
            // 5xx - response
            println("Error: ${e.response.status.description}")
            ResultNetwork.failure(exception = e, statusCode = e.response.status.value)
        } catch (e: UnresolvedAddressException) {
            println("Error: ${e.printStackTrace()}")
            ResultNetwork.failure(exception = e, statusCode = null)
        } catch (e: SerializationException) {
            println("Error: ${e.printStackTrace()}")
            ResultNetwork.failure(exception = e, statusCode = null)
        } catch (e: Exception) {
            println("Error: ${e.printStackTrace()}")
            ResultNetwork.failure(exception = e, statusCode = null)
        }
    }
}
