package ru.evdokimova.imagesnasa.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.evdokimova.imagesnasa.data.entity.PageEntity

@Dao
interface PageDao {

    @Query("SELECT * FROM pages LIMIT 1")
    suspend fun getPage(): PageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity)

    @Query("DELETE FROM pages")
    suspend fun deleteAllPages()
}