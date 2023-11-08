package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


    interface AsteroidService {
        @GET("neo/rest/v1/feed")
        suspend fun getAsteroids(
            @Query("api_key") api_key: String
        ): String

        @GET("planetary/apod")
        suspend fun getPictureOfTheDay(
            @Query("api_key") api_key: String
        ): PictureOfDay
    }

    object AsteroidApi {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()


        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val retrofitService: AsteroidService by lazy { retrofit.create(AsteroidService::class.java) }
    }