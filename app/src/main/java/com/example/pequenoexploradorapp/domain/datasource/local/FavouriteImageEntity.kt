package com.example.pequenoexploradorapp.domain.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String?,
    val dateCreated: String?,
    val creators: String? = null,
    val isFavourite: Boolean = false
)