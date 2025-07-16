package com.example.thecatapi_sword.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.thecatapi_sword.database.AppDatabase
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.FavouriteBreedEntity
import com.example.thecatapi_sword.model.FavouriteRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "cat_db"
    ).build().favoriteBreedDao()

    private val repository = FavouriteRepository(dao)

    var favourites = mutableStateOf<List<BreedEntity>>(emptyList())
        private set

    var isFavourite = mutableStateOf(false)
        private set

    init {
        loadFavourites()
    }

    fun loadFavourites() {
        viewModelScope.launch {
            favourites.value = dao.getFavouriteBreeds()
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

    val averageMinLifeSpan = mutableStateOf(0.0)

    fun calculateAverageLifeSpan() {
        val list = favourites.value

        val minLifespans = list.mapNotNull { breed ->
            val min = breed.life_span.split("-").firstOrNull()?.trim()?.toIntOrNull()
            min
        }

        if (minLifespans.isNotEmpty()) {
            val average = minLifespans.average()
            averageMinLifeSpan.value = average
        } else {
            averageMinLifeSpan.value = 0.0
        }
    }
}
