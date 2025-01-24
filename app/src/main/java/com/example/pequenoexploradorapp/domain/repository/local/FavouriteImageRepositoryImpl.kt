package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.FavouriteImageToSave
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageDao
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FavouriteImageRepositoryImpl(
    private val dao: FavouriteImageDao
): FavouriteImageRepository {

    override suspend fun getFavouriteImage(): List<FavouriteImageToSave> {
        val response = dao.findAll().map { it.toFavouriteImage() }
        return response
    }

    override suspend fun saveImage(image: FavouriteImageToSave) = withContext(IO) {
        dao.save(image.toFavouriteImageEntity())
    }

    override suspend fun findById(id: String): Unit = withContext(IO) {
        dao.findById(id)
    }

    override suspend fun deleteImage(image: FavouriteImageToSave) = withContext(IO) {
        dao.deleteImageFavourite(image.toFavouriteImageEntity())
    }

    override suspend fun deleteAll() = withContext(IO) {
        dao.deleteAll()
    }
}


fun FavouriteImageToSave.toFavouriteImageEntity() = FavouriteImageEntity(
    id = this.id,
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    link = this.link,
    isFavourite = this.isFavourite
)

fun FavouriteImageEntity.toFavouriteImage() = FavouriteImageToSave(
    id = this.id,
    title = this.title,
    dateCreated = this.dateCreated,
    link = this.link,
    creators = this.creators,
    keywords = null,
    isFavourite = this.isFavourite
)