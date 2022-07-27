package ru.evdokimova.imagesnasa.data.models

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.evdokimova.imagesnasa.data.models.raw.ImageAsObjectSerializer


@Serializable(with = ImageAsObjectSerializer::class)
data class Image(
    @PrimaryKey
    val nasaId: String,
    val title: String,
    val dateCreated: String,
    val description: String,
    val location: String?,
    val href: String,
    val imageAssets: String
)