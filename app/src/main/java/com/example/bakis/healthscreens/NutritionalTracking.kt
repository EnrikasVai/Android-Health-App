package com.example.bakis.healthscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.composables.HealthBox
import com.example.bakis.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun NutritionalTracking(navController: NavController, viewModel: HomeViewModel = hiltViewModel()){
    //date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    //water
    val userId by viewModel.userId.collectAsState()
    val dailyWaterIntakeTotals = viewModel.getDailyWaterIntakeTotalsForUser(userId)
        .collectAsState(initial = emptyList()).value
    val waterPerDayMin = dailyWaterIntakeTotals.map { it.second.toFloat() }
    val todaysWaterIntake by viewModel.totalDailyIntake.collectAsState()

    // Determine if data is still loading
    val isLoading = remember { mutableStateOf(true) }
    val isInitialLoad = remember { mutableStateOf(true) }

    val dailyWaterIntakeTotalsState = viewModel.getDailyWaterIntakeTotalsForUser(userId).collectAsState(initial = emptyList())
    val monthlyWaterIntakeTotalsState = viewModel.getMonthlyWaterIntakeAveragesForUser(userId).collectAsState(initial = emptyList())

    // Observe changes in your data fetching states and update isLoading accordingly
    LaunchedEffect(dailyWaterIntakeTotalsState.value, monthlyWaterIntakeTotalsState.value) {
        if (isInitialLoad.value) {
            // Only apply the delay for the initial load
            delay(50)
            isInitialLoad.value = false
        }
        isLoading.value = dailyWaterIntakeTotalsState.value.isEmpty() && monthlyWaterIntakeTotalsState.value.isEmpty()
    }


    //nutrition
    val todayNutrition by viewModel.todayCalories.collectAsState()
    val weeksNutrition by viewModel.weeklyNutritionCounts.collectAsState()
    val weeksNutritionFloats = weeksNutrition.map { it.toFloat() }

    LaunchedEffect(key1 = userId) {
        viewModel.fetchWaterIntakeRecords(userId)
        viewModel.fetchDailyWaterIntakeForUser(userId, today)
    }
    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {


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
                        color = 0xFFfca46a
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Water consumed",
                        subtitle = "Last 7 days",
                        detail = "$todaysWaterIntake ml",
                        detailSub = "Today",
                        caloriesPerDay = waterPerDayMin,
                        navController = navController,
                        navigateTo = "waterIntakeScreen",
                        color = 0xFFfca46a
                    )
                    }
                }
            }
        }
    }
}
