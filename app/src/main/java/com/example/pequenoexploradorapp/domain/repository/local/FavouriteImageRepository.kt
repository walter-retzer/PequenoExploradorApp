package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.ImageToLoad
import com.example.pequenoexploradorapp.data.NasaImageData

interface FavouriteImageRepository {
    suspend fun getFavouriteImage(): List<ImageToLoad>

    suspend fun save(images: ImageToLoad)
}
