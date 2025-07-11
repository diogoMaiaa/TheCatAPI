package com.example.thecatapi_sword.view


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thecatapi_sword.ui.theme.TheCatAPI_SwordTheme

sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Listar : BottomNavItem("listar", "Listar", Icons.Default.List)
    object Favoritos : BottomNavItem("favoritos", "Favoritos", Icons.Default.Favorite)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(BottomNavItem.Listar, BottomNavItem.Favoritos)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar (
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 20.dp)
            .clip(RoundedCornerShape(50.dp))
            .fillMaxWidth(),
        tonalElevation = 6.dp
    ){
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreviewWrapper() {
    TheCatAPI_SwordTheme {
        val navController = rememberNavController()
        BottomNavBar(navController = navController)
    }
}
