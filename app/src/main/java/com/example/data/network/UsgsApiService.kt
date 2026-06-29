package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface UsgsApiService {
    @GET("fdsnws/event/1/query")
    suspend fun getVenezuelaEarthquakes(
        @Query("format") format: String = "geojson",
        @Query("minlatitude") minLat: Double = 1.0,
        @Query("maxlatitude") maxLat: Double = 13.0,
        @Query("minlongitude") minLon: Double = -74.0,
        @Query("maxlongitude") maxLon: Double = -59.0,
        @Query("orderby") orderBy: String = "time",
        @Query("limit") limit: Int = 100
    ): UsgsResponse

    companion object {
        private const val BASE_URL = "https://earthquake.usgs.gov/"

        fun create(): UsgsApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(UsgsApiService::class.java)
        }
    }
}
