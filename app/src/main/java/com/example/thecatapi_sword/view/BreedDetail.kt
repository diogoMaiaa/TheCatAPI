package com.example.thecatapi_sword.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.thecatapi_sword.model.BreedDetail
import com.example.thecatapi_sword.model.TheCatAPI
import com.example.thecatapi_sword.viewmodel.BreedViewModel

@Composable
fun BreedDetailScreen(navController: NavController, breedId: String, viewModel: BreedViewModel = viewModel()) {
    val breedState = produceState<BreedDetail?>(initialValue = null, breedId) {
        try {
            val response = TheCatAPI.api.getBreedById(breedId)
            this.value = if (response.isSuccessful) {
                val breed = response.body()
                if (breed != null) {
                    BreedDetail(
                        name = breed.name,
                        origin = breed.origin,
                        reference_image_id = breed.reference_image_id,
                        temperament = breed.temperament,
                        description = breed.description
                    )
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("BreedDetailScreen", "Erro ao carregar dados: ${e.message}")
            this.value = null
        }
    }

    val breed = breedState.value

    if (breed == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var imageUrl by remember(breed.reference_image_id) { mutableStateOf<String?>(null) }
    LaunchedEffect(breed.reference_image_id) {
        if (breed.reference_image_id.isNotBlank()) {
            imageUrl = viewModel.getImageUrl(breed.reference_image_id)
        }
    }


    var isFavorite by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFececec))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFececec))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = breed.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = breed.name,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Origin: ${breed.origin}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Temperament: ${breed.temperament}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = breed.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


