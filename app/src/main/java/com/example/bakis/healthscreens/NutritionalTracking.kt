package com.example.bakis.healthscreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.composables.HealthBox
import com.example.bakis.viewmodel.HomeViewModel


@Composable
fun NutritionalTracking(navController: NavController, viewModel: HomeViewModel = hiltViewModel()){
    val todayNutrition by viewModel.todayCalories.collectAsState()
    val weeksNutrition by viewModel.weeklyNutritionCounts.collectAsState()
    val weeksNutritionFloats = weeksNutrition.map { it.toFloat() }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Nutrition",
                showBackButton = true,

            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item{
                Column(modifier = Modifier.padding(top = 30.dp, start = 10.dp)) {
                    Text(text = "Data", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(start = 10.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Calories consumed",
                        subtitle = "Last 7 days",
                        detail = "${todayNutrition.toInt()} cal",
                        detailSub = "Today",
                        caloriesPerDay = weeksNutritionFloats,
                        navController = navController,
                        navigateTo = "nutritionCalories",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Water consumed",
                        subtitle = "Last 7 days",
                        detail = "200 ml",
                        detailSub = "Today",
                        caloriesPerDay = weeksNutritionFloats,
                        navController = navController,
                        navigateTo = "waterIntakeScreen",
                        color = 0xFFA6DECD
                    )
                    }
                }
            }
        }
}
