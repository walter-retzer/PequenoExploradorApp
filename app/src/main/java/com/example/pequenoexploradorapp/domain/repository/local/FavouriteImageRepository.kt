package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.FavouriteImageToSave

interface FavouriteImageRepository {
    suspend fun getFavouriteImage(): List<FavouriteImageToSave>

    suspend fun save(images: FavouriteImageToSave)
}
