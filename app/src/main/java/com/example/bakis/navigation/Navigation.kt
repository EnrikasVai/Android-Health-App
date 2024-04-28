package com.example.bakis.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakis.graphscreen.CaloriesScreen
import com.example.bakis.graphscreen.DistanceScreen
import com.example.bakis.graphscreen.HeartRateRestingScreen
import com.example.bakis.graphscreen.HeartRateScreen
import com.example.bakis.graphscreen.MoveMinutesScreen
import com.example.bakis.graphscreen.NutritionCaloriesScreen
import com.example.bakis.graphscreen.SleepScreen
import com.example.bakis.graphscreen.SpeedScreen
import com.example.bakis.graphscreen.StepScreen
import com.example.bakis.graphscreen.WaterIntakeScreen
import com.example.bakis.healthscreens.ExerciseTracking
import com.example.bakis.healthscreens.HeartRateZones
import com.example.bakis.healthscreens.NutritionalTracking
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
        composable("heartRateZones") {
            HeartRateZones(navController, homeViewModel)
        }
        composable("heartRateResting") {
            HeartRateRestingScreen(navController, homeViewModel)
        }
        composable("exerciseTracking") {
            ExerciseTracking(navController, homeViewModel)
        }
        composable("nutritionCalories") {
            NutritionCaloriesScreen(navController, homeViewModel)
        }
        composable("moveMinutes") {
            MoveMinutesScreen(navController, homeViewModel)
        }
        composable("distance") {
            DistanceScreen(navController, homeViewModel)
        }
        composable("speed") {
            SpeedScreen(navController, homeViewModel)
        }
        composable("nutritionalTracking") {
            NutritionalTracking(navController, homeViewModel)
        }
        composable("caloriesScreen"){
            CaloriesScreen(navController = navController)
        }
        composable("waterIntakeScreen"){
            WaterIntakeScreen(navController = navController)
        }
    }
}


