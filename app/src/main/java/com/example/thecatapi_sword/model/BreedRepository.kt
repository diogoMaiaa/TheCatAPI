package com.example.thecatapi_sword.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BreedRepository(
    private val api: BreedClient,
    private val dao: BreedDao,
    private val context: Context
) {
    private val apiKey = "live_JDriEcLesUnLBnHQTi2DtJA40PxeMtvCJFYgBfuXblVyiJgV34bU9ByR07NAGeJK"

    suspend fun syncBreedsFromApi() {
        if (isOnline()) {
            val response = api.getAllBreeds(apiKey = apiKey, limit = 1000, page = 0)
            if (response.isSuccessful) {

                val breeds = response.body()?.map {
                    val imageurl = getImageUrl(it.reference_image_id)
                    BreedEntity(
                        id = it.id,
                        name = it.name,
                        reference_image_id = it.reference_image_id ?: "",
                        origin = it.origin,
                        temperament = it.temperament,
                        description = it.description,
                        imageUrl = imageurl
                    )
                } ?: emptyList()

                withContext(Dispatchers.IO) {
                    dao.clearAll()
                    dao.insertAll(breeds)
                }
            }
        }
    }

    suspend fun getBreedsFromDb(): List<BreedEntity> {
        return withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    suspend fun getImageUrl(imageId: String): String? {
        return try {
            val response = api.getImageById(imageId)
            if (response.isSuccessful) response.body()?.url else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getBreedFromDbById(breedId: String): BreedEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(breedId)
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}
