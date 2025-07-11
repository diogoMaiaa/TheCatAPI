package com.example.thecatapi_sword.view.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Preview(showBackground = true)
@Composable
fun BreedDetailScreen() {
    val breed = BreedUiMock(
        name = "Maine Coon",
        imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
        origin = "United States",
        temperament = "Adaptable, Intelligent, Loving, Gentle, Independent",
        description = "The Maine Coon is one of the largest domesticated cat breeds. It has a distinctive physical appearance and is known for its friendly and intelligent nature."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(breed.imageUrl),
            contentDescription = breed.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = breed.name,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Origem: ${breed.origin}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Temperamento: ${breed.temperament}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = breed.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

data class BreedUiMock(
    val name: String,
    val imageUrl: String,
    val origin: String,
    val temperament: String,
    val description: String
)
