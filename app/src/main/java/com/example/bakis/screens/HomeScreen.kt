package com.example.bakis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.AnimatedNPieChart
import com.example.bakis.R
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class InfoData(
    val iconId: Int,
    val text: String,
    val value: String,
    val color: Long,
    val nav: String
)
data class StepData(
    val iconId: Int,
    val text: String,
    val value: String,
    val color: Long,
    val nav: String,
    val goal: Int,
    val min: Double,
    val speed: Double,
    val distance: Double
)
data class PieData(
    val label: String,
    val value: Int,
    val color: Color,
)

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavHostController) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
    val todaysWaterIntake by homeViewModel.totalDailyIntake.collectAsState()
    val userName by homeViewModel.userName.collectAsState()

    val sleepCount by homeViewModel.sleepCount.collectAsState()
    //format sleep data
    val hours = sleepCount.toInt() / 60
    val remainingMinutes = sleepCount.toInt() % 60
    //
    val calCount by homeViewModel.calCount.collectAsState()
    val bpmCount by homeViewModel.bpmCount.collectAsState()
    val roundedBpmCount = bpmCount.toFloat().roundToInt()

    val data = listOf(
        InfoData(R.drawable.heart_beat, "Heart Rate", "$roundedBpmCount bpm",0xFFFF3131,"bpmData"),
        InfoData(R.drawable.glass_water, "Water Intake", "$todaysWaterIntake ml", 0xFF1c37ff,"waterIntakeScreen") ,
        InfoData(R.drawable.bed, "Sleep", "${hours}h ${remainingMinutes}min", 0xFF09bfe8,"sleepData"),
        InfoData(R.drawable.calories, "Calories", "$calCount kcal",0xFFf52749,"caloriesScreen"),
    )

    val stepCount by homeViewModel.stepCount.collectAsState()
    val userStepGoal by homeViewModel.userStepGoal.collectAsState()


    val movedMinutes by homeViewModel.todayMoveMinutes.collectAsState()
    val moveSpeed by homeViewModel.todayAverageSpeed.collectAsState()
    val movedDistance by homeViewModel.todayDistance.collectAsState()

    val stepBoxData = StepData(R.drawable.footsteps, "Steps, Activity", stepCount, 0xFFFF7518, "stepData", userStepGoal, movedMinutes, moveSpeed, movedDistance)
    val userId by homeViewModel.userId.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    LaunchedEffect(key1 = userId) {
        homeViewModel.fetchWaterIntakeRecords(userId)
        homeViewModel.fetchDailyWaterIntakeForUser(userId, today)
        homeViewModel.fetchBpmCount()
        homeViewModel.fetchCalCount()
        homeViewModel.fetchFitnessData()
        homeViewModel.fetchStepCount()
        homeViewModel.fetchSleepCount()
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Health Dashboard"
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = items,
                icons = icons
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .background(Color(0xFF262626)), // Set the background color here

        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Welcome, ${userName.ifEmpty { "User" }}", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                StepBox(stepBoxData, navController)
                data.chunked(2).forEach { pairList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        pairList.forEach { infoData ->
                            InfoBox(
                                infoData = infoData,
                                navController = navController,
                            )
                            if (infoData != pairList.last()) Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
@Composable
fun StepBox(
    infoData: StepData,
    navController: NavHostController
){
    val iconColor = Color(infoData.color)
    val textColor = Color(infoData.color)
    val navTag = infoData.nav
    val goal = infoData.goal

    val stepCount = infoData.value

    val goalAchieved = stepCount.toInt()
    val goalLeft = goal- goalAchieved

    val pieDataPoints = listOf(
        PieData("Goal", goalAchieved, color = Color(0xFFFF7518)),
        PieData("Goal-left", goalLeft, color = Color(0xFFD3D3D3))
    )
    Column (modifier = Modifier.padding(bottom = 8.dp).shadow(8.dp, RoundedCornerShape(10.dp)).clickable { navController.navigate(navTag) }){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(356.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(Color(0xFF333333))
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(170.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = infoData.iconId),
                        contentDescription = infoData.text,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = infoData.text,
                        color = textColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp)
                    )
                }
                Text(
                    text = infoData.value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "Today",
                    color = iconColor,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                )
            }
            //Donut CHART
            Box(
                modifier = Modifier
                    .width(190.dp)
                    .height(185.dp)
                    .padding(16.dp)
            ) {
                AnimatedNPieChart(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp),
                    pieDataPoints = pieDataPoints
                ) {
                    Row(
                        modifier = it
                            .padding(20.dp)
                            .fillMaxSize()
                            .padding(16.dp)
                            .scale(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Goal",
                                color = Color(0xFFFF7518),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "$goal",
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

        }
        Row(
            modifier = Modifier
                .width(356.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(color = Color(0xFF333333))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${infoData.min.toInt()}", color = Color.White, fontSize = 20.sp)
                Text(text = "Min", color = Color.White)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val formattedDistance = String.format("%.2f", infoData.distance / 1000)
                Text(text = formattedDistance, color = Color.White, fontSize = 20.sp)
                Text(text = "Km", color = Color.White)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val distanceInKm = infoData.distance / 1000
                val timeInHours = infoData.min / 60

                val speedKmH = if (timeInHours > 0) distanceInKm / timeInHours else 0.0

                val formattedSpeed = String.format("%.2f", speedKmH)
                Text(text = formattedSpeed, color = Color.White, fontSize = 20.sp)
                Text(text = "km/h", color = Color.White)
            }
        }
    }

}
@Composable
fun InfoBox(
    infoData: InfoData,
    navController: NavHostController,
) {
    val iconColor = Color(infoData.color)
    val textColor = Color(infoData.color)
    val navTag = infoData.nav

    Box(
        modifier = Modifier
            .size(170.dp)
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF333333))
            .clickable { navController.navigate(navTag) },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Icon(
                painter = painterResource(id = infoData.iconId),
                contentDescription = infoData.text,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = infoData.text,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp)
            )
        }
        Text(
            text = infoData.value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        Text(
            text = "Today",
            color = iconColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        )
    }
}