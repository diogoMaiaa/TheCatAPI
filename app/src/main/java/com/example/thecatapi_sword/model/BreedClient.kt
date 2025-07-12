package com.example.thecatapi_sword.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreedClient {
    @GET("breeds")
    suspend fun getAllBreeds(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 8,
        @Query("page") page: Int = 0
    ): Response<List<Breed>>

    @GET("images/{image_id}")
    suspend fun getImageById(
        @Path("image_id") imageId: String
    ): Response<ImageData>
}
