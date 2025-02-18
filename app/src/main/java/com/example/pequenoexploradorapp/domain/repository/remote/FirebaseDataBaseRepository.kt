package com.example.pequenoexploradorapp.domain.repository.remote

import com.example.pequenoexploradorapp.data.FirebaseDataBaseResponse
import com.example.pequenoexploradorapp.data.ResponseFirebase
import kotlinx.coroutines.flow.Flow

interface FirebaseDataBaseRepository {
    fun getMessagesFlow(): Flow<ResponseFirebase<FirebaseDataBaseResponse>>

}
