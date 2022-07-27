package ru.evdokimova.imagesnasa.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.evdokimova.imagesnasa.data.entity.ImageEntity

@Dao
interface ImageDao {

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<ImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)

    @Query("DELETE FROM images")
    suspend fun deleteAllImages()
}