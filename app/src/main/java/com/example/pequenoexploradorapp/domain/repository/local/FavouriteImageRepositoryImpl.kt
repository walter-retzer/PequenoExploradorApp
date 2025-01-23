package com.example.pequenoexploradorapp.domain.repository.local

import com.example.pequenoexploradorapp.data.ImageToLoad
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageDao
import com.example.pequenoexploradorapp.domain.datasource.local.FavouriteImageEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FavouriteImageRepositoryImpl(
    private val dao: FavouriteImageDao
): FavouriteImageRepository {

    override suspend fun getFavouriteImage(): List<ImageToLoad> {
        val response = dao.findAll().map { it.toFavouriteImage() }
        return response
    }

    override suspend fun save(images: ImageToLoad) = withContext(IO) {
        dao.save(images.toFavouriteImageEntity())
    }
}


fun ImageToLoad.toFavouriteImageEntity() = FavouriteImageEntity(
    title = this.title,
    dateCreated = this.dateCreated,
    creators = this.creators,
    link = this.link,
    isFavourite = this.isFavourite
)

fun FavouriteImageEntity.toFavouriteImage() = ImageToLoad(
    title = this.title,
    dateCreated = this.dateCreated,
    link = this.link,
    creators = this.creators,
    keywords = null,
    isFavourite = this.isFavourite
)