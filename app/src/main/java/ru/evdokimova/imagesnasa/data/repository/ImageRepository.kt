package ru.evdokimova.imagesnasa.data.repository

import android.util.Log
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import ru.evdokimova.imagesnasa.data.api.ImageApi
import javax.inject.Inject

private const val TAG = "ImageRepository "

class ImageRepository @Inject constructor(private val imageApi: ImageApi) {

    suspend fun getAssetsImage(nasaId: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val response = imageApi.getAssetsImage(nasaId)

            if (response.isSuccessful) {
                val json =
                    response.body()!!.jsonObject["collection"]!!.jsonObject["items"]!!.jsonArray

                for (item in json) {
                    val value = item.jsonObject["href"].toString()
                    if (value.contains(".jpg"))
                        list.add(value.replace("\"", ""))
                }
            }
        } catch (t: Throwable) {
            Log.d(TAG, "${t.message} ${Log.getStackTraceString(t)}")
        }
        return list
    }
}