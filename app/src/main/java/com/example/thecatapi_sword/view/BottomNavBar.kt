package com.example.thecatapi_sword.view

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Listar : BottomNavItem("listar", "Listar", Icons.Default.List)
    object Favoritos : BottomNavItem("favoritos", "Favoritos", Icons.Default.Favorite)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    navController: NavController?,
    activity: ComponentActivity
) {
    val context = LocalContext.current
    val items = listOf(BottomNavItem.Listar, BottomNavItem.Favoritos)

    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 20.dp)
            .clip(RoundedCornerShape(50.dp))
            .fillMaxWidth(),
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        when (item.route) {
                            BottomNavItem.Listar.route -> {
                                if (context !is MainActivity) {
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                    activity.finish()
                                }
                            }
                            BottomNavItem.Favoritos.route -> {
                                if (context !is Favourites) {
                                    context.startActivity(Intent(context, Favourites::class.java))
                                    activity.finish()
                                }
                            }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
