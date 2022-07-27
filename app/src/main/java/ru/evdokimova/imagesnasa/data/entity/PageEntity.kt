package ru.evdokimova.imagesnasa.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class PageEntity (
    @PrimaryKey
    val id: Int = 0,
    val totalCount: Int,
    val prevPage: Int?,
    val nextPage: Int?,
    val query: String
)