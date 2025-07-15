package com.example.thecatapi_sword.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_breeds")
data class FavouriteBreedEntity(
    @PrimaryKey val id: String
)
