package com.example.thecatapi_sword.view

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.thecatapi_sword.model.BreedRepository
import com.example.thecatapi_sword.model.TheCatAPI
import com.example.thecatapi_sword.database.AppDatabase
import com.example.thecatapi_sword.ui.theme.TheCatAPI_SwordTheme
import com.example.thecatapi_sword.viewmodel.BreedViewModelFactory
import com.example.thecatapi_sword.viewmodel.FavouriteViewModel
import com.example.thecatapi_sword.viewmodel.FavouriteViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecatapi_sword.model.FavouriteRepository

class Favourites : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheCatAPI_SwordTheme {
                val context = LocalContext.current.applicationContext as Application

                val breedFactory = remember {
                    BreedViewModelFactory(
                        context,
                        BreedRepository(
                            TheCatAPI.api,
                            AppDatabase.getDatabase(context).breedDao(),
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
                            currentRoute = BottomNavItem.Favourites.route,
                            navController = navController,
                            activity = this
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Favourites.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Favourites.route) {
                            FavouriteScreen(navController = navController)
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
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current.applicationContext as Application
    val database = AppDatabase.getDatabase(context)
    val repository = FavouriteRepository(database.favoriteBreedDao())

    val factory = remember {
        FavouriteViewModelFactory(context, repository)
    }

    val viewModel: FavouriteViewModel = viewModel(factory = factory)

    val favourites by viewModel.favourites
    val averageMin by remember { viewModel.averageMinLifeSpan }

    LaunchedEffect(Unit) {
        viewModel.loadFavourites()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Favourites",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Average LifeSpan: ${String.format("%.1f", averageMin)} years",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .testTag("favourites_grid"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(favourites) { _, breed ->
                val isFavoriteState = remember(breed.id) {
                    mutableStateOf(true)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("favourite_item_${breed.id}")
                ) {
                    GridMenuItem(
                        imageUrl = breed.imageUrl,
                        isFavorite = isFavoriteState,
                        breedId = breed.id,
                        favoriteViewModel = viewModel,
                        onClick = { navController.navigate("details/${breed.id}") }
                    )
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { navController.navigate("details/${breed.id}") }
                    )
                }
            }
        }

    }
}
