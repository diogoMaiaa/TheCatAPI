package com.example.thecatapi_sword.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BreedDao {
    @Query("SELECT * FROM breeds")
    suspend fun getAll(): List<BreedEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(breeds: List<BreedEntity>)

    @Query("DELETE FROM breeds")
    suspend fun clearAll()

    @Query("SELECT * FROM breeds WHERE id = :breedId LIMIT 1")
    suspend fun getById(breedId: String): BreedEntity?

}

