package com.example.pequenoexploradorapp.domain.network


sealed class ResultNetwork<out T> {
    data class Success<out T>(val data: T, val statusCode: Int) : ResultNetwork<T>()
    data class Failure(val exception: Throwable, val statusCode: Int?) : ResultNetwork<Nothing>()

    companion object {
        fun <T> success(data: T, statusCode: Int): ResultNetwork<T> = Success(data, statusCode)
        fun failure(exception: Throwable, statusCode: Int?): ResultNetwork<Nothing> =
            Failure(exception, statusCode)
    }
}
