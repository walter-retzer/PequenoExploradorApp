package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.NasaImageData

interface FavouriteImageRepository {
    suspend fun getFavouriteImage(): List<NasaImageData>

    suspend fun save(images: NasaImageData)
}
