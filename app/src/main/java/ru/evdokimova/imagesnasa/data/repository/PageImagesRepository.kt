package ru.evdokimova.imagesnasa.data.repository

import android.util.Log
import androidx.room.withTransaction
import ru.evdokimova.imagesnasa.data.api.ImageApi
import ru.evdokimova.imagesnasa.data.db.NasaDatabase
import ru.evdokimova.imagesnasa.data.entity.ImageEntity
import ru.evdokimova.imagesnasa.data.models.Page
import ru.evdokimova.imagesnasa.utils.Resource
import javax.inject.Inject

private const val TAG = "PageImagesRepository "

class PageImagesRepository @Inject constructor(
    private val imageApi: ImageApi,
    private val db: NasaDatabase,
    private val pageImagesMapper: PageImagesMapper
) {

    private val imageDao = db.imageDao()
    private val pageDao = db.pageDao()

    private suspend inline fun getDbImages() = imageDao.getAllImages()

    suspend fun getResourceImagesFromDB() = try {
        Resource.Success(getDbImages())
    } catch (t: Throwable) {
        Log.e(TAG, "${t.message} ${Log.getStackTraceString(t)}")
        Resource.Error("${t.message}")
    }

    suspend fun getNewResourceImages(
        query: String,
        pageN: Int,
    ): Resource<List<ImageEntity>> =
        try {
            val response = imageApi.getSearchImages(query, page = pageN)
            if (response.isSuccessful) {
                if (response.body()!!.page.images.isNotEmpty()) {
                    saveFetchResult(response.body()!!.page, query)
                    Resource.Success(getDbImages())
                } else {
                    Resource.Error("Images not found")
                }
            } else {
                Log.e("$TAG error, code", "${response.code()}")
                Resource.Error("Error, code ${response.code()}")
            }
        } catch (t: Throwable) {
            Log.e(TAG, "${t.message} ${Log.getStackTraceString(t)}")
            Resource.Error("${t.message}")
        }

    suspend fun getPage() = try {
        pageDao.getPage()
    } catch (t: Throwable) {
        Log.e(TAG, "${t.message} ${Log.getStackTraceString(t)}")
        null
    }

    private suspend fun saveFetchResult(page: Page, query: String) {
        db.withTransaction {
            imageDao.deleteAllImages()
            imageDao.insertImages(page.images.map { image ->
                pageImagesMapper.imageModelToEntity(image)
            })
            pageDao.deleteAllPages()
            pageDao.insertPage(pageImagesMapper.pageModelToEntity(page, query))
        }
    }


}