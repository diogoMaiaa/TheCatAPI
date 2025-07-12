package com.example.thecatapi_sword.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TheCatAPI {
    private const val BASE_URL = "https://api.thecatapi.com/v1/"

    val api: BreedClient by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BreedClient::class.java)
    }
}
