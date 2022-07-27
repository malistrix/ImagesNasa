package ru.evdokimova.imagesnasa.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.evdokimova.imagesnasa.data.entity.ImageEntity
import ru.evdokimova.imagesnasa.data.entity.PageEntity

@Database(entities = [ImageEntity::class, PageEntity::class], version = 1)
abstract class NasaDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao
    abstract fun pageDao(): PageDao
}