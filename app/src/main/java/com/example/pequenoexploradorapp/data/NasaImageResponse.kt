package com.example.pequenoexploradorapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NasaImageResponse(
    @SerialName("collection") val collection: NasaImageCollection
)

@Serializable
data class NasaImageCollection(
    @SerialName("href") val href: String?,
    @SerialName("version") val version: String?,
    @SerialName("items") val items: List<NasaImageItems>? = null,
    @SerialName("links") val links: List<NasaImageNextPage>? = null,
    @SerialName("metadata") val metadata: NasaImageMetaData?
)

@Serializable
data class NasaImageItems(
    @SerialName("href") val href: String?,
    @SerialName("data") val data: List<NasaImageData?>,
    @SerialName("links") val links: List<NasaImageLink?>,
    @SerialName("isFavourite") var isFavourite: Boolean = false
)

@Serializable
data class NasaImageData(
    @SerialName("title") var title: String?,
    @SerialName("date_created") val dateCreated: String?,
    @SerialName("secondary_creator") val creators: String? = null,
    @SerialName("keywords") val keywords: List<String>? = null
)

@Serializable
data class NasaImageLink(
    @SerialName("href") val href: String?
)

@Serializable
data class NasaImageNextPage(
    @SerialName("href") val nextPage: String?
)

@Serializable
data class NasaImageMetaData(
    @SerialName("total_hits") val totalHits: Int = 0
)

@Serializable
data class FavouriteImageToSave(
    val id: Long,
    val title: String? = null,
    val dateCreated: String? = null,
    val link: String? = null,
    val creators: String? = null,
    val keywords: List<String>? = null,
    var isFavourite: Boolean = false
)