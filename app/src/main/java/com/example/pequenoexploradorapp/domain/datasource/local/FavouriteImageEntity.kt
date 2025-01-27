package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteImageEntity(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var title: String?,
    val dateCreated: String?,
    val creators: String? = null,
    val link: String?,
    val isFavourite: Boolean = false
)