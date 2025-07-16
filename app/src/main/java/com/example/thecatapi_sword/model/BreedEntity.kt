package com.example.thecatapi_sword.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val reference_image_id: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val life_span: String,
    val imageUrl: String? = null
)
