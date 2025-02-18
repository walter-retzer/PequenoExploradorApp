package com.example.pequenoexploradorapp.domain.repository.remote

import com.example.pequenoexploradorapp.data.FirebaseDataBaseResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class FirebaseDataBaseRepositoryImpl : FirebaseDataBaseRepository {

    private val database = FirebaseDatabase.getInstance().reference
    override fun getMessagesFlow(): Flow<FirebaseDataBaseResponse> = callbackFlow {
        val messageRef = database.child("message")
        try {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val response = snapshot.getValue<FirebaseDataBaseResponse>()
                    println("$response")
                    response?.let { trySend(it) }
                }
                override fun onCancelled(error: DatabaseError) {
                    println("$error")
                    close(error.toException())
                }
            }
            messageRef.addValueEventListener(listener)
            awaitClose { messageRef.removeEventListener(listener) }
        } catch (e: Exception) {
            println("$e")
        }
    }
}
