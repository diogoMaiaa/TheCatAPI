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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.thecatapi_sword.database.AppDatabase
import com.example.thecatapi_sword.model.FavouriteBreedEntity
import com.example.thecatapi_sword.model.FavouriteRepository
import com.example.thecatapi_sword.ui.theme.TheCatAPI_SwordTheme
import com.example.thecatapi_sword.viewmodel.BreedViewModel
import com.example.thecatapi_sword.viewmodel.BreedViewModelFactory
import com.example.thecatapi_sword.viewmodel.FavouriteViewModel
import com.example.thecatapi_sword.viewmodel.FavouriteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContent {
            TheCatAPI_SwordTheme {
                val context = LocalContext.current.applicationContext as android.app.Application

                val breedFactory = remember {
                    BreedViewModelFactory(
                        context,
                        com.example.thecatapi_sword.model.BreedRepository(
                            com.example.thecatapi_sword.model.TheCatAPI.api,
                            com.example.thecatapi_sword.database.AppDatabase.getDatabase(context).breedDao(),
                            context
                        )
                    )
                }

                val database = AppDatabase.getDatabase(context)
                val repository = FavouriteRepository(database.favoriteBreedDao())

                val factory = remember {
                    FavouriteViewModelFactory(context, repository)
                }

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
                            GridMenuScreen(
                                navController = navController,
                                breedViewModelFactory = breedFactory,
                                favouriteViewModelFactory = factory
                            )
                        }
                        composable("details/{breedId}") { backStackEntry ->
                            val breedId = backStackEntry.arguments?.getString("breedId") ?: ""
                            BreedDetailScreen(
                                navController = navController,
                                breedId = breedId,
                                breedViewModelFactory = breedFactory,
                                favouriteViewModelFactory = factory
                            )
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
    breedViewModelFactory: ViewModelProvider.Factory,
    favouriteViewModelFactory: ViewModelProvider.Factory,
    viewModel: BreedViewModel = viewModel(factory = breedViewModelFactory),
    favoriteViewModel: FavouriteViewModel = viewModel(factory = favouriteViewModelFactory)
) {
    var searchQuery by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()

    val breeds = viewModel.getPagedBreeds(searchQuery)
    val currentPage = viewModel.currentPage
    val totalPages = viewModel.totalPages
    val isLoading = viewModel.isLoading

    LaunchedEffect(currentPage, searchQuery) {
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("loading_indicator"))
            }
        } else if (breeds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No breed found.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(breeds) { _, breed ->
                    val isFavoriteState = remember { mutableStateOf(false) }

                    LaunchedEffect(breed.id) {
                        isFavoriteState.value = favoriteViewModel.isFavorite(breed.id)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GridMenuItem(
                            imageUrl = breed.imageUrl,
                            isFavorite = isFavoriteState,
                            favoriteViewModel = favoriteViewModel,
                            breedId = breed.id,
                            onClick = { navController.navigate("details/${breed.id}") }
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
}


@Composable
fun GridMenuItem(
    imageUrl: String?,
    isFavorite: MutableState<Boolean>,
    breedId: String,
    favoriteViewModel: FavouriteViewModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("breed_item_$breedId")
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: "")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .testTag("breed_image_$breedId")
        )

        IconButton(
            onClick = {
                val favorite = FavouriteBreedEntity(id = breedId)
                if (isFavorite.value) {
                    favoriteViewModel.deleteFavorite(favorite)
                } else {
                    favoriteViewModel.insertFavorite(favorite)
                }
                isFavorite.value = !isFavorite.value
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .testTag("favourite_icon_$breedId")
        ) {
            Icon(
                imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite.value) Color.Red else Color.Gray
            )
        }
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
