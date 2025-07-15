package com.example.thecatapi_sword.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.thecatapi_sword.database.AppDatabase
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.BreedRepository
import com.example.thecatapi_sword.model.TheCatAPI
import kotlinx.coroutines.launch

class BreedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BreedRepository

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
        val db = AppDatabase.getDatabase(context)
        repository = BreedRepository(TheCatAPI.api, db.breedDao(), context)

        viewModelScope.launch {
            try {
                isLoading = true
                repository.syncBreedsFromApi()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _allBreeds.clear()
                _allBreeds.addAll(repository.getBreedsFromDb())


                val imageLoader = ImageLoader(context)
                _allBreeds.forEach { breed ->
                    val request = ImageRequest.Builder(context)
                        .data(breed.imageUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build()
                    imageLoader.enqueue(request)
                }

                isLoading = false
            }
        }
    }

    fun fetchBreeds(page: Int) {
        currentPage = page.coerceIn(0, totalPages - 1)
    }

    fun getPagedBreeds(searchQuery: String): List<BreedEntity> {
        val filtered = _allBreeds.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(filtered.size)

        return if (start < end) filtered.subList(start, end) else emptyList()
    }

    suspend fun getBreedById(breedId: String): BreedEntity? {
        return repository.getBreedFromDbById(breedId)
    }
}
