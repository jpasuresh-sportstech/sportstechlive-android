package com.sportstechbrands.sportstechlive.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.sportstechbrands.sportstechlive.data.local.TokenManager
import com.sportstechbrands.sportstechlive.data.network.RetrofitClient
import com.sportstechbrands.sportstechlive.ui.components.BottomNavItem
import com.sportstechbrands.sportstechlive.ui.screens.*
import com.sportstechbrands.sportstechlive.ui.screens.auth.*

// ─────────────────────────────────────────────────────────────────────────────
// Route constants
// ─────────────────────────────────────────────────────────────────────────────

object AuthRoutes {
    const val LOGIN           = "login"
    const val SIGNUP          = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
}

object Routes {
    const val HOME       = "home"
    const val WORKOUTS   = "workouts"
    const val QUICKSTART = "quickstart"
    const val COMMUNITY  = "community"
    const val PROFILE    = "profile"
}

val authRouteSet = setOf(AuthRoutes.LOGIN, AuthRoutes.SIGNUP, AuthRoutes.FORGOT_PASSWORD)

// ─────────────────────────────────────────────────────────────────────────────
// Bottom nav items
// ─────────────────────────────────────────────────────────────────────────────

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME,       "Home",      Icons.Outlined.Home,          Icons.Filled.Home),
    BottomNavItem(Routes.WORKOUTS,   "Workouts",  Icons.Outlined.FitnessCenter, Icons.Filled.FitnessCenter),
    BottomNavItem(Routes.QUICKSTART, "Start",     Icons.Outlined.FlashOn,       Icons.Filled.FlashOn),
    BottomNavItem(Routes.COMMUNITY,  "Community", Icons.Outlined.Groups,        Icons.Filled.Groups),
    BottomNavItem(Routes.PROFILE,    "Profile",   Icons.Outlined.Person,        Icons.Filled.Person)
)

// ─────────────────────────────────────────────────────────────────────────────
// Nav host
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SportstechNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }

    // Initialize Retrofit with the token provider once
    DisposableEffect(Unit) {
        RetrofitClient.init { tokenManager.accessToken }
        onDispose { }
    }

    // Skip login if already authenticated
    val startDestination = if (tokenManager.isLoggedIn()) Routes.HOME else AuthRoutes.LOGIN

    NavHost(
        navController    = navController,
        startDestination = startDestination,
        modifier         = modifier
    ) {
        // ── Auth ──────────────────────────────────────────────────────
        composable(AuthRoutes.LOGIN) {
            LoginScreen(navController)
        }
        composable(AuthRoutes.SIGNUP) {
            SignupScreen(navController)
        }
        composable(AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController)
        }

        // ── Main app ──────────────────────────────────────────────────
        composable(Routes.HOME)       { HomeScreen() }
        composable(Routes.WORKOUTS)   { WorkoutsScreen() }
        composable(Routes.QUICKSTART) { QuickstartScreen() }
        composable(Routes.COMMUNITY)  { CommunityScreen() }
        composable(Routes.PROFILE)    { ProfileScreen(navController) }
    }
}

fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState    = true
    }
}
