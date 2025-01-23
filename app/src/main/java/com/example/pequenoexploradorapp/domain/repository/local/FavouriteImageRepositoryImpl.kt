package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.NasaImageData
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageDao
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FavouriteImageRepositoryImpl(
    private val dao: FavouriteImageDao
): FavouriteImageRepository {

    override suspend fun getFavouriteImage(): List<NasaImageData> {
        val response = dao.findAll().map { it.toNasaImage() }
        return response
    }

    override suspend fun save(images: NasaImageData) = withContext(IO) {
        dao.save(images.toNasaImageEntity())
    }
}

fun NasaImageData.toNasaImageEntity() = FavouriteImageEntity(
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    isFavourite = this.isFavourite
)

fun FavouriteImageEntity.toNasaImage() = NasaImageData(
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    isFavourite = this.isFavourite
)
