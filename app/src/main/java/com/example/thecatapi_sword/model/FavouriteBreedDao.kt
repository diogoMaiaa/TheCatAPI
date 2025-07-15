package com.example.thecatapi_sword.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteBreedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breed: FavouriteBreedEntity)

    @Delete
    suspend fun delete(breed: FavouriteBreedEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_breeds WHERE id = :breedId)")
    suspend fun isFavorite(breedId: String): Boolean

    @Query("""
        SELECT b.* FROM breeds b
        INNER JOIN favourite_breeds f ON b.id = f.id
    """)
    suspend fun getFavouriteBreeds(): List<BreedEntity>

}