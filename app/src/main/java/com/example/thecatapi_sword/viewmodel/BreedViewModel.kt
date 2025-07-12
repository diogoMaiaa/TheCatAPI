package com.example.thecatapi_sword.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecatapi_sword.model.Breed
import com.example.thecatapi_sword.model.BreedRepository
import kotlinx.coroutines.launch

class BreedViewModel : ViewModel() {

    private val repository = BreedRepository()

    var breeds by mutableStateOf<List<Breed>>(emptyList())
        private set

    var currentPage by mutableStateOf(0)
        private set

    var totalPages by mutableStateOf(1)
        private set

    private val limitPerPage = 8

    init {
        viewModelScope.launch {
            val total = repository.getTotalBreeds()

            totalPages = if (total % limitPerPage == 0) {
                total / limitPerPage
            } else {
                total / limitPerPage + 1
            }

            fetchBreeds(0)
        }
    }

    fun fetchBreeds(page: Int) {
        if (page < 0 || page >= totalPages) return

        currentPage = page
        viewModelScope.launch {
            val result = repository.getBreeds(page)
            breeds = result ?: emptyList()
        }
    }

    private val imageCache = mutableMapOf<String, String>()

    suspend fun getImageUrl(imageId: String): String? {
        if (imageCache.containsKey(imageId)) {
            return imageCache[imageId]
        }

        val url = repository.getImageUrl(imageId)
        if (url != null) {
            imageCache[imageId] = url
        }
        return url
    }




}
