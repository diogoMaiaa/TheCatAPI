package com.example.thecatapi_sword.view


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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.FavouriteBreedEntity
import com.example.thecatapi_sword.viewmodel.BreedViewModel
import com.example.thecatapi_sword.viewmodel.FavouriteViewModel
import kotlinx.coroutines.launch

@Composable
fun BreedDetailScreen(
    navController: NavController,
    breedId: String,
    breedViewModelFactory: ViewModelProvider.Factory,
    favouriteViewModelFactory: ViewModelProvider.Factory,
    favoriteViewModel: FavouriteViewModel = viewModel(factory = favouriteViewModelFactory)
) {

    val viewModel: BreedViewModel = viewModel(factory = breedViewModelFactory)

    val breed by produceState<BreedEntity?>(initialValue = null, breedId) {
        value = viewModel.getBreedById(breedId)

    }

    if (breed == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Breed Not Found.")
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(breedId) {
        isFavorite = favoriteViewModel.isFavorite(breedId)
    }

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
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = {
                    val favorite = FavouriteBreedEntity(
                        id = breed!!.id,

                    )

                    coroutineScope.launch {
                        if (isFavorite) {
                            favoriteViewModel.deleteFavorite(favorite)
                        } else {
                            favoriteViewModel.insertFavorite(favorite)
                        }
                        isFavorite = !isFavorite
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFececec))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favourite",
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
                .testTag("breed_image_${breed!!.id}")
        ) {
            Image(
                painter = rememberAsyncImagePainter(breed!!.imageUrl),
                contentDescription = breed!!.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = breed!!.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Origin: ${breed!!.origin}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Temperament: ${breed!!.temperament}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = breed!!.description, style = MaterialTheme.typography.bodyMedium)
    }
}

