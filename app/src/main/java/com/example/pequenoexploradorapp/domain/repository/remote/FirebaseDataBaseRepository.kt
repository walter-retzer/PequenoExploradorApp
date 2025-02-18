package com.example.pequenoexploradorapp.domain.repository.remote

import com.example.pequenoexploradorapp.data.FirebaseDataBaseResponse
import kotlinx.coroutines.flow.Flow

interface FirebaseDataBaseRepository {
    fun getMessagesFlow(): Flow<FirebaseDataBaseResponse>

}