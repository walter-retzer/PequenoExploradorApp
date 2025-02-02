package com.example.pequenoexploradorapp.domain.network


sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T, val statusCode: Int) : ApiResponse<T>()
    data class Failure(
        val exception: Throwable,
        val statusCode: Int?,
        val messageError: String
    ) : ApiResponse<Nothing>()

    companion object {
        fun <T> success(data: T, statusCode: Int): ApiResponse<T> = Success(data, statusCode)
        fun failure(
            exception: Throwable,
            statusCode: Int?,
            messageError: String
        ): ApiResponse<Nothing> = Failure(exception, statusCode, messageError)
    }
}
