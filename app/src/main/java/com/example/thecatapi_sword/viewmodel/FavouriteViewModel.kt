package com.example.thecatapi_sword.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.FavouriteBreedEntity
import com.example.thecatapi_sword.model.FavouriteRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(
    application: Application,
    private val repository: FavouriteRepository
) : AndroidViewModel(application) {

    var favourites = mutableStateOf<List<BreedEntity>>(emptyList())
        private set

    var isFavourite = mutableStateOf(false)
        private set

    val averageMinLifeSpan = mutableStateOf(0.0)

    init {
        loadFavourites()
    }

    fun loadFavourites() {
        viewModelScope.launch {
            favourites.value = repository.getFavouriteBreeds()
            calculateAverageLifeSpan()
        }
    }

    fun insertFavorite(breed: FavouriteBreedEntity) {
        viewModelScope.launch {
            repository.insert(breed)
            isFavourite.value = true
            loadFavourites()
        }
    }

    fun deleteFavorite(breed: FavouriteBreedEntity) {
        viewModelScope.launch {
            repository.delete(breed)
            isFavourite.value = false
            loadFavourites()
        }
    }

    suspend fun isFavorite(breedId: String): Boolean {
        return repository.isFavorite(breedId)
    }

    fun calculateAverageLifeSpan() {
        val list = favourites.value
        val minLifespans = list.mapNotNull {
            it.life_span.split("-").firstOrNull()?.trim()?.toIntOrNull()
        }
        averageMinLifeSpan.value = if (minLifespans.isNotEmpty()) {
            minLifespans.average()
        } else {
            0.0
        }
    }
}
