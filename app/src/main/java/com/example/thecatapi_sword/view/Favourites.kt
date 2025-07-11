package com.example.thecatapi_sword.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.thecatapi_sword.ui.theme.TheCatAPI_SwordTheme
import com.example.thecatapi_sword.view.ui.theme.BreedDetailScreen
import androidx.navigation.NavController


class Favourites : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheCatAPI_SwordTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentRoute = BottomNavItem.Favoritos.route,
                            navController = navController,
                            activity = this
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Favoritos.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Favoritos.route) {
                            FavouriteScreen(navController = navController)
                        }
                        composable("details") {
                            BreedDetailScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavouriteScreen(modifier: Modifier = Modifier, navController: NavController) {
    val menuItems = listOf(
        "Siamês", "Persa", "Maine Coon", "Bengal",
        "Sphynx", "Ragdoll", "Abyssinian", "British Shorthair"
    )

    val imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg"
    val favoriteIndices = listOf(0, 1, 2, 3, 4, 5, 6, 7)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Favoritos",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 20.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(menuItems) { index, title ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GridMenuItem(
                        imageUrl = imageUrl,
                        isFavorite = index in favoriteIndices,
                        onClick = { navController.navigate("details") }
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
