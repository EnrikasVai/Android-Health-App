package com.example.bakis.healthscreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.math.roundToInt

@Composable
fun HeartRateZones(navController: NavController, viewModel: HomeViewModel = hiltViewModel()){
    val bpmCount by viewModel.bpmCount.collectAsState()
    val numericBpmCount = bpmCount.toFloatOrNull() ?: 0f
    val roundedBpmCount = numericBpmCount.roundToInt()
    val bpmString = roundedBpmCount.toString()
    val bpmPerDay by viewModel.weeklyHeartRateCounts.collectAsState()

    val bpmRestingToday by viewModel.bpmCountResting.collectAsState()
    val numericBpmCountResting = bpmRestingToday.toFloatOrNull() ?: 0f
    val roundedBpmCountResting = numericBpmCountResting.roundToInt()
    val bpmStringResting = roundedBpmCountResting.toString()
    val bpmRestingWeek by viewModel.weeklyHeartRateCountsResting.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBpmCount()
        viewModel.fetchWeeklyHeartRateCount()
        viewModel.fetchMonthlyHeartRateCounts()
        viewModel.fetchBpmCountResting()
        viewModel.fetchWeeklyHeartRateCountResting()
        viewModel.fetchMonthlyHeartRateCountsResting()
        Log.d("MyHealthApp", "Heart data fetched")
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Heart Rate Vitals",
                showBackButton = true,

                )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF262626))
        ) {
            item{
                Column(modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp)) {
                    Text(text = "DATA", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(start = 10.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "BPM Averages",
                        subtitle = "Last 7 days",
                        detail = "$bpmString bpm",
                        detailSub = "Today",
                        caloriesPerDay = bpmPerDay,
                        navController = navController,
                        navigateTo = "bpmData",
                        color = 0xFFfca46a
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "BPM Resting",
                        subtitle = "Last 7 days",
                        detail = if (bpmStringResting.isNotEmpty() && bpmStringResting != "0") {
                            "$bpmStringResting bpm"
                        } else {
                            "No Data"
                        },
                        detailSub = if (bpmStringResting.isNotEmpty() && bpmStringResting != "0") {
                            "Yesterday"
                        } else {
                            ""
                        },
                        caloriesPerDay = bpmRestingWeek,
                        navController = navController,
                        navigateTo = "heartRateResting",
                        color = 0xFFfca46a
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    /*
                    HealthBox(
                        title = "Blood Oxygen",
                        subtitle = "Last 7 days",
                        detail = "98%",
                        detailSub = "Today",
                        caloriesPerDay = weeksNutritionFloats,
                        navController = navController,
                        navigateTo = "waterIntakeScreen",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    */
                }
            }
        }
    }
}