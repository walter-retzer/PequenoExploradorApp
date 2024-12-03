package com.example.pequenoexploradorapp.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode


class NetworkRepository(private val client: HttpClient) {

    private suspend inline fun <reified T> makeRequest(crossinline request: suspend () -> HttpResponse): ResultNetwork<T> {
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
        } catch (e: Exception) {
            println("Error: ${e.printStackTrace()}")
            ResultNetwork.failure(exception = e, statusCode = null)
        }
    }
}