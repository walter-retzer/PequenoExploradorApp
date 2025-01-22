package com.example.pequenoexploradorapp.domain.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pequenoexploradorapp.data.NasaImageData
import com.example.pequenoexploradorapp.data.NasaImageLink
import kotlinx.serialization.SerialName
import java.util.UUID

@Entity
data class NasaImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String?,
    val dateCreated: String?,
    val creators: String? = null,
    val isFavourite: Boolean = false
)