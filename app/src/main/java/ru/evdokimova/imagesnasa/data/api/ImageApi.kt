package ru.evdokimova.imagesnasa.data.api

import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.evdokimova.imagesnasa.data.models.raw.RawImagesResponse
import ru.evdokimova.imagesnasa.utils.Constants.Companion.DEFAULT_PAGE

interface ImageApi {
    @GET("search")
    suspend fun getSearchImages(
        @Query("q") query: String,
        @Query("media_type") type:String = "image",
        @Query("page") page: Int = DEFAULT_PAGE
    ): Response<RawImagesResponse>

    @GET("/asset/{nasa_id}")
    suspend fun getAssetsImage(
        @Path("nasa_id") nasaId: String
    ): Response<JsonObject>
}