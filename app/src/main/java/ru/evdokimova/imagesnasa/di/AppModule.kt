package ru.evdokimova.imagesnasa.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.evdokimova.imagesnasa.data.api.ImageApi
import ru.evdokimova.imagesnasa.data.db.NasaDatabase
import ru.evdokimova.imagesnasa.utils.ConnectivityLiveData
import ru.evdokimova.imagesnasa.utils.Constants.Companion.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun baseUrl() = BASE_URL

    /*
        Используется для логирования запроса в консоли
     */
    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC)
//        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun provideOkhttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): ImageApi {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(baseUrl)
            .client(provideOkhttpClient())
            .build()
            .create(ImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            NasaDatabase::class.java,
            "nasa_database"
        ).build()


    @Provides
    @Singleton
    fun provideCheckInternet(@ApplicationContext appContext: Context) =
        ConnectivityLiveData(appContext)
}