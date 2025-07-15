package com.example.thecatapi_sword.model

class FavouriteRepository(private val dao: FavouriteBreedDao) {

    suspend fun insert(breed: FavouriteBreedEntity) = dao.insert(breed)
    suspend fun delete(breed: FavouriteBreedEntity) = dao.delete(breed)
    suspend fun isFavorite(breedId: String): Boolean = dao.isFavorite(breedId)

}