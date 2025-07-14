package com.example.thecatapi_sword.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.thecatapi_sword.ui.theme.TheCatAPI_SwordTheme
import com.example.thecatapi_sword.viewmodel.BreedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContent {
            TheCatAPI_SwordTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentRoute = BottomNavItem.List.route,
                            navController = navController,
                            activity = this
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.List.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.List.route) {
                            GridMenuScreen(navController = navController)
                        }
                        composable("details/{breedId}") { backStackEntry ->
                            val breedId = backStackEntry.arguments?.getString("breedId")
                            BreedDetailScreen(navController = navController, breedId = breedId ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridMenuScreen(
    navController: NavController,
    viewModel: BreedViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val breeds = viewModel.breeds
    val currentPage = viewModel.currentPage
    val totalPages = viewModel.totalPages
    val gridState = rememberLazyGridState()

    LaunchedEffect(currentPage) {
        gridState.scrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Cats APP",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search...") },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 12.dp)
        )

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(breeds) { _, breed ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GridMenuItem(
                        imageId = breed.reference_image_id ?: "",
                        isFavorite = false,
                        onClick = { navController.navigate("details/${breed.id}") },
                        viewModel = viewModel
                    )
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                PaginationControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPrevious = { viewModel.fetchBreeds(currentPage - 1) },
                    onNext = { viewModel.fetchBreeds(currentPage + 1) }
                )
            }
        }
    }
}

@Composable
fun GridMenuItem(
    imageId: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    viewModel: BreedViewModel
) {
    var imageUrl by remember(imageId) { mutableStateOf<String?>(null) }

    LaunchedEffect(imageId) {
        if (imageId.isNotBlank()) {
            imageUrl = viewModel.getImageUrl(imageId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: "")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Favourite",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious, enabled = currentPage > 0) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = "${currentPage + 1} / $totalPages",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = onNext, enabled = currentPage < totalPages - 1) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}
