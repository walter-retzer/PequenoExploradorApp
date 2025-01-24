package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.FavouriteImageToSave

interface FavouriteImageRepository {
    suspend fun getFavouriteImage(): List<FavouriteImageToSave>
    suspend fun saveImage(image: FavouriteImageToSave)
    suspend fun findById(id: String)
    suspend fun deleteImage(image: FavouriteImageToSave)
    suspend fun deleteAll()
}
