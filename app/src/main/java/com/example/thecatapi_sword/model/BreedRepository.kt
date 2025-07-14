package com.example.thecatapi_sword.model

class BreedRepository {
    private val apiKey = "live_JDriEcLesUnLBnHQTi2DtJA40PxeMtvCJFYgBfuXblVyiJgV34bU9ByR07NAGeJK"

    suspend fun getBreeds(page: Int): List<Breed>? {
        val response = TheCatAPI.api.getAllBreeds(
            apiKey = apiKey,
            page = page,
            limit = 8
        )
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getTotalBreeds(): Int {
        val response = TheCatAPI.api.getAllBreeds(apiKey = apiKey, limit = 1000, page = 0)
        return response.body()?.size ?: 0
    }

    suspend fun getImageUrl(imageId: String): String? {
        val response = TheCatAPI.api.getImageById(imageId)
        return if (response.isSuccessful) response.body()?.url else null
    }

    suspend fun getBreedById(breedId: String): BreedDetail?{
        val response = TheCatAPI.api.getBreedById(breedId)
        return if (response.isSuccessful) response.body() else null
    }

}
