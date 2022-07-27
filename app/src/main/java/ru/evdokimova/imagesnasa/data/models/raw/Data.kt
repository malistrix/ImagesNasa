package ru.evdokimova.imagesnasa.data.models.raw

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("nasa_id")
    val nasaId: String,

    val title: String,

    @SerialName("date_created")
    val dateCreated: String,

    val description: String,

    val location: String = ""
)