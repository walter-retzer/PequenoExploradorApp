package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "favourites")
data class FavouriteImageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var title: String?,
    val dateCreated: String?,
    val creators: String? = null,
    val link: String?,
    val isFavourite: Boolean = false
)