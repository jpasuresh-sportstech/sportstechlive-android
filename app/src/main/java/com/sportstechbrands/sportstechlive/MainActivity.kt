package com.sportstechbrands.sportstechlive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sportstechbrands.sportstechlive.navigation.*
import com.sportstechbrands.sportstechlive.ui.components.GlassBottomNavigation
import com.sportstechbrands.sportstechlive.ui.theme.SportstechLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportstechLiveTheme {
                SportstechApp()
            }
        }
    }
}

@Composable
fun SportstechApp() {
    val navController  = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route ?: AuthRoutes.LOGIN

    // Hide bottom nav on auth screens
    val showBottomNav  = currentRoute !in authRouteSet

    Box(modifier = Modifier.fillMaxSize()) {
        SportstechNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        )

        if (showBottomNav) {
            GlassBottomNavigation(
                items        = bottomNavItems,
                currentRoute = currentRoute,
                onItemClick  = { route -> navController.navigateSingleTop(route) },
                modifier     = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}
