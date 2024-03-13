package com.example.bakis.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakis.graphscreen.HeartRateScreen
import com.example.bakis.graphscreen.SleepScreen
import com.example.bakis.graphscreen.StepScreen
import com.example.bakis.screens.FirstTimeScreen
import com.example.bakis.screens.HealthScreen
import com.example.bakis.screens.HomeScreen
import com.example.bakis.screens.ProfileScreen
import com.example.bakis.screens.WelcomeScreen
import com.example.bakis.viewmodel.HomeViewModel


@Composable
fun SetupNavigation(navController: NavHostController, homeViewModel: HomeViewModel) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("home") {
            // Pass `navController` to `HomeScreen` here
            HomeScreen(navController = navController)
        }
        composable("health") {
            HealthScreen(navController = navController, onNavigate = { route ->
                navController.navigate(route)
            })
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("registration") {
            FirstTimeScreen(homeViewModel, navController)
        }
        composable("welcome") {
            WelcomeScreen(homeViewModel, navController)
        }
        composable("stepData") {
            StepScreen(navController, homeViewModel)
        }
        composable("sleepData"){
            SleepScreen(navController, homeViewModel)
        }
        composable("bpmData") {
            HeartRateScreen(navController, homeViewModel)
        }
        // Add any additional destinations here
    }
}
