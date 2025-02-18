package com.example.pequenoexploradorapp.data


sealed class ResponseFirebase<out T> {
    data class Success<out T>(
        val data: T
    ): ResponseFirebase<T>()

    data class Failure(
        val e: Exception
    ): ResponseFirebase<Nothing>()
}
