package com.example.pequenoexploradorapp.network

import android.util.Log
import com.example.pequenoexploradorapp.BuildConfig
import com.example.pequenoexploradorapp.data.RoverMission
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


class NetworkRepository {



//    suspend fun getRoverMission(): ResultNetwork<RoverMission> =
//        makeRequest {
//            provideHttpClientModule.get {
//                //url(HttpRoutes.BASE_URL_IMAGES)
//                parameter("api_key", BuildConfig.API_KEY)
//            }
//        }

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
