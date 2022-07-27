package ru.evdokimova.imagesnasa.data.models

import kotlinx.serialization.Serializable
import ru.evdokimova.imagesnasa.data.models.raw.PageSerializer

@Serializable(with = PageSerializer::class)
data class Page(
    val images: List<Image>,
    val totalCountImages: Int,
    val prevPage: Int?,
    val nextPage: Int?,
)