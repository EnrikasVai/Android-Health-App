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
fun ExerciseTracking(navController: NavController, viewModel: HomeViewModel = hiltViewModel()){
    val stepCount by viewModel.stepCount.collectAsState()
    val stepCountWeek by viewModel.weeklyStepCounts.collectAsState()
    val stepsPerDayFloats = stepCountWeek.map { it.toFloat() }

    val caloriesCount by viewModel.calCount.collectAsState()
    val caloriesCountWeek by viewModel.weeklyCaloriesCounts.collectAsState()

    val minCount by viewModel.todayMoveMinutes.collectAsState()
    val minWeekCount by viewModel.weeklyMoveMinutes.collectAsState()
    val minWeekCountFloats = minWeekCount.map { it.toFloat() }

    val distanceToday by viewModel.todayDistance.collectAsState()
    val distanceWeek by viewModel.weeklyDistance.collectAsState()

    val todayMoveHours = minCount / 60.0
    val todaySpeedKmH = if (todayMoveHours > 0) ((distanceToday / 1000) / todayMoveHours).toDouble() else 0.0
    val todaySpeedKmHRounded = String.format("%.2f", todaySpeedKmH).toDouble()
    val weeklySpeedsKmH = minWeekCountFloats.zip(distanceWeek).map { (dailyMinutes, dailyDistance) ->
        val dailyHours = dailyMinutes / 60.0 // Convert minutes to hours
        if (dailyHours > 0) ((dailyDistance / 1000) / dailyHours).toFloat() else 0f // Convert to km/h for each day
    }
    val weeklySpeedsKmHRounded = weeklySpeedsKmH.map {
        String.format("%.2f", it).toFloat()
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Activity Tracking",
                showBackButton = true,
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item{
                Column(modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp, bottom = 50.dp)) {
                    Text(text = "INSIGHTS", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(start = 10.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Steps",
                        subtitle = "Last 7 days",
                        detail = "$stepCount steps",
                        detailSub = "Today",
                        caloriesPerDay = stepsPerDayFloats,
                        navController = navController,
                        navigateTo = "stepData",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Calories burned",
                        subtitle = "Last 7 days",
                        detail = "$caloriesCount cal",
                        detailSub = "Today",
                        caloriesPerDay = caloriesCountWeek,
                        navController = navController,
                        navigateTo = "caloriesScreen",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Move minutes",
                        subtitle = "Last 7 days",
                        detail = "${minCount.toInt()} min",
                        detailSub = "Today",
                        caloriesPerDay = minWeekCountFloats,
                        navController = navController,
                        navigateTo = "moveMinutes",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Move speed",
                        subtitle = "Last 7 days",
                        detail = "$todaySpeedKmHRounded km/h",
                        detailSub = "Today",
                        caloriesPerDay = weeklySpeedsKmHRounded,
                        navController = navController,
                        navigateTo = "speed",
                        color = 0xFFA6DECD
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    HealthBox(
                        title = "Distance",
                        subtitle = "Last 7 days",
                        detail = "%.2f km".format(distanceToday / 1000.0),
                        detailSub = "Today",
                        caloriesPerDay = distanceWeek,
                        navController = navController,
                        navigateTo = "distance",
                        color = 0xFFA6DECD
                    )
                }
            }
        }
    }
}