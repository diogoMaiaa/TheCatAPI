package com.example.thecatapi_sword.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.BreedRepository
import kotlinx.coroutines.launch

class BreedViewModel(
    application: Application,
    private val repository: BreedRepository,
    private val preloadImages: Boolean = true
) : AndroidViewModel(application) {

    private val _allBreeds = mutableStateListOf<BreedEntity>()
    val breeds: List<BreedEntity> get() = _allBreeds

    var currentPage by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(true)
        private set

    val pageSize = 8
    val totalPages: Int
        get() = (_allBreeds.size + pageSize - 1) / pageSize

    init {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            isLoading = true


            if (shouldSync(context)) {
                try {
                    val success = repository.syncBreedsFromApi()
                    if (success) {
                        saveLastSync(context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


            _allBreeds.clear()
            _allBreeds.addAll(repository.getBreedsFromDb())

            if (preloadImages) {
                val imageLoader = ImageLoader(context)
                _allBreeds.forEach { breed ->
                    val request = ImageRequest.Builder(context)
                        .data(breed.imageUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build()
                    imageLoader.enqueue(request)
                }
            }

            isLoading = false
        }
    }


    fun fetchBreeds(page: Int) {
        currentPage = page.coerceIn(0, totalPages - 1)
    }

    fun getPagedBreeds(searchQuery: String): List<BreedEntity> {
        val filtered = _allBreeds.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        val maxPage = (filtered.size + pageSize - 1) / pageSize
        if (currentPage >= maxPage) {
            currentPage = 0
        }

        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(filtered.size)

        return if (start < end) filtered.subList(start, end) else emptyList()
    }


    suspend fun getBreedById(breedId: String): BreedEntity? {
        return repository.getBreedFromDbById(breedId)
    }

    private fun shouldSync(context: Context) : Boolean {
        val intervalMinutes = 10L
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        val lastSync = prefs.getLong("last_breed_sync", 0L)
        val now = System.currentTimeMillis()
        return (now - lastSync) > intervalMinutes * 60 * 1000
    }


    private fun saveLastSync(context: Context) {
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("last_breed_sync", System.currentTimeMillis()).apply()
    }

}
