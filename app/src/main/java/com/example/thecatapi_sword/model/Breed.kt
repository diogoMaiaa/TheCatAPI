package com.example.thecatapi_sword.model

data class Breed(
    var id: String,
    val name: String,
    val reference_image_id: String,
    val origin: String,
    val temperament: String,
    val description: String
)
