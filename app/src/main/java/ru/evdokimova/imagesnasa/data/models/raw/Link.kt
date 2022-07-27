package ru.evdokimova.imagesnasa.data.models.raw

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val href: String,
    val rel: String,
)