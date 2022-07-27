package ru.evdokimova.imagesnasa.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "images")
data class ImageEntity (
    @PrimaryKey
    val nasaId: String,
    val title: String,
    val dateCreated: String,
    val description: String,
    val location: String?,
    val href: String,
    val imageAssets: String
): Serializable
