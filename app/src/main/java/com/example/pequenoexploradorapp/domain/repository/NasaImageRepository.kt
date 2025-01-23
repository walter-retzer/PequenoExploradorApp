package com.example.pequenoexploradorapp.domain.repository

import com.example.pequenoexploradorapp.data.NasaImageData
import com.example.pequenoexploradorapp.domain.database.NasaImageDao
import com.example.pequenoexploradorapp.domain.database.NasaImageEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class NasaImageRepository(
    private val dao: NasaImageDao
) {

    suspend fun getFavouriteImage(): List<NasaImageData> {
        val response = dao.findAll().map { it.toNasaImage() }
        return response
    }

    suspend fun save(images: NasaImageData) = withContext(IO) {
        dao.save(images.toNasaImageEntity())
    }
}

fun NasaImageData.toNasaImageEntity() = NasaImageEntity(
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    isFavourite = this.isFavourite
)

fun NasaImageEntity.toNasaImage() = NasaImageData(
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    isFavourite = this.isFavourite
)
