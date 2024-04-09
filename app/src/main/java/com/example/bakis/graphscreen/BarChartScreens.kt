package com.example.bakis.graphscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.addCaloriesToGoogleFit
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.composables.StepProgressBar
import com.example.bakis.composables.WaterIntakeTracker
import com.example.bakis.viewmodel.HomeViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.BaseAxis
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.String.format
import java.util.Calendar


fun calculateWeekDays(): List<String> {
    val daysOfWeek = listOf("Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY is 1
    return (0 until 7).map { i ->
        daysOfWeek[(today - i + 7) % 7]
    }.reversed()
}

fun calculateMonths(): List<String> {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val calendar = Calendar.getInstance()
    val thisMonth = calendar.get(Calendar.MONTH)
    return (0 until 12).map { i ->
        months[(thisMonth - i + 12) % 12]
    }.reversed()
}

val daysOfWeek = calculateWeekDays()
private val bottomAxisValueFormatter =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        daysOfWeek[x.toInt() % daysOfWeek.size]
    }

val months = calculateMonths()
private val bottomAxisValueFormatterMonth =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        months[x.toInt() % months.size]
    }

@Composable
fun StepScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val stepCountToday by viewModel.stepCount.collectAsState()
    val userStepGoal by viewModel.userStepGoal.collectAsState()

    //data example Steps
    val stepsPerDay: List<Int> by viewModel.weeklyStepCounts.collectAsState()
    val stepsPerDayFloats = stepsPerDay.map { it.toFloat() }
    val stepsPerMonth by viewModel.monthlyStepCounts.collectAsState()
    val averageStepsDay = stepsPerDay.filter { it > 0 }.average()
    val averageStepsMonthBox = stepsPerMonth.filter { it > 0 }.average()

    val markerText = "Average Steps:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Steps Data",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                    StepProgressBar(stepCountToday.toInt(),userStepGoal, viewModel)
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                        Text(
                            text="Average Steps: ${String.format("%.0f", averageStepsDay)}",
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                        else
                            Text(
                                text="Average Steps: ${averageStepsMonthBox.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = stepsPerDayFloats,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xffff5500,
                            markerText = "Steps:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = stepsPerMonth,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xffff5500,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xffff5500) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Steps are a useful measure of how much you're moving around, and can help you spot changes in your activity levels",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}
@Composable
fun SleepScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val sleepPerDayMin: List<Int> by viewModel.weeklySleepCounts.collectAsState()
    val sleepPerDayFloats = sleepPerDayMin.map { it.toFloat() }
    val sleepPerMonthMin by viewModel.monthlySleepCounts.collectAsState()
    val averageSleepDay = sleepPerDayMin.filter { it > 0 }.average()
    val averageSleepMonth = sleepPerMonthMin.filter { it > 0 }.average()

    val averageSleepDayHours = (averageSleepDay / 60).toInt()
    val averageSleepDayMinutes = (averageSleepDay % 60).toInt()
    val formattedAverageSleepDay = "${averageSleepDayHours}h ${averageSleepDayMinutes}m"

    val averageSleepMonthHours = (averageSleepMonth / 60).toInt()
    val averageSleepMonthMinutes = (averageSleepMonth % 60).toInt()
    val formattedAverageSleepMonth = "${averageSleepMonthHours}h ${averageSleepMonthMinutes}m"
    // Converts each value to hours
    val sleepPerDayHours = sleepPerDayFloats.map { it / 60 }
    val sleepPerMonthHours = sleepPerMonthMin.map { it.toFloat() / 60 }



    val markerText = "Average Sleep:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Sleep Data",
                onEditClick = { /* ... */ },
                showEditIcon = false,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average Sleep: $formattedAverageSleepDay",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average Sleep: $formattedAverageSleepMonth",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = sleepPerDayHours,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xFF09bfe8,
                            markerText = "Sleep:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = sleepPerMonthHours,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xFF09bfe8,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFF09bfe8) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = if (label == selectedLabel.value) Color.Black else Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Duration shows your total time slept each night. Most Healthy adults need between 7 and 9 hours.",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun CaloriesScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    //data example Calories
    val caloriesPerDayMin by viewModel.weeklyCaloriesCounts.collectAsState()
    val caloriesPerMonthMin by viewModel.monthlyCaloriesCounts.collectAsState()
    val averageCaloriesDay = caloriesPerDayMin.filter { it > 0 }.average()
    val averageCaloriesMonth = caloriesPerMonthMin.filter { it > 0 }.average()

    // Determine if data is still loading
    val isLoading = remember { mutableStateOf(true) }
    val isInitialLoad = remember { mutableStateOf(true) }
    val markerText = "Average Calories Burned:"


    LaunchedEffect(caloriesPerDayMin, caloriesPerMonthMin) {
        if (isInitialLoad.value) {
            // Only apply the delay for the initial load
            delay(50)
            isInitialLoad.value = false
        }
        isLoading.value = caloriesPerDayMin.isEmpty() && caloriesPerMonthMin.isEmpty()
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center // This aligns the contents of the Box (including the Column) to the center
        ) {
            CircularProgressIndicator()
        }
    } else {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Calories Burned",
                onEditClick = { /* ... */ },
                showEditIcon = false,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if (selectedLabel.value == "Week") {
                            Text(
                                text = "Average Calories Burned: ${averageCaloriesDay.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        } else {
                            Text(
                                text = "Average Calories Burned: ${averageCaloriesMonth.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = caloriesPerDayMin,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xFFf52749,
                            markerText = "Calories Burned:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = caloriesPerMonthMin,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xFFf52749,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFFf52749) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Your body uses energy for more than just workouts. You'll see an estimate of your total calories burned through the day. Active + Rest combined",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}}
@Composable
fun WaterIntakeScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {

    val markerText = "Average Water Intake:"

    val userId by viewModel.userId.collectAsState()
    val dailyWaterIntakeTotals = viewModel.getDailyWaterIntakeTotalsForUser(userId)
        .collectAsState(initial = emptyList()).value
    val monthlyWaterIntakeTotals = viewModel.getMonthlyWaterIntakeAveragesForUser(userId)
        .collectAsState(initial = emptyList()).value
    val waterPerMonthMin = monthlyWaterIntakeTotals.map { it.second.toFloat() }
    val waterPerDayMin = dailyWaterIntakeTotals.map { it.second.toFloat() }

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

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center // This aligns the contents of the Box (including the Column) to the center
        ) {
            CircularProgressIndicator()
        }
    } else {

    val averageWaterDay = waterPerDayMin.average()
    val averageWaterMonth = waterPerMonthMin.average()
    var averageWaterCount = averageWaterDay.toInt()



    val labels = listOf("Week", "Month")
    val selectedLabel = remember { mutableStateOf("Week") }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Water Intake",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(top = 10.dp, start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    WaterIntakeTracker(viewModel)
                    Box(modifier = Modifier
                        .padding(10.dp, top = 30.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text="Average Water Intake: $averageWaterCount ml",
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    if(selectedLabel.value == "Week") {
                        averageWaterCount = averageWaterMonth.toInt() // Correct way to assign value
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = waterPerDayMin,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xFF1c37ff,
                            markerText = "Water Intake:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        averageWaterCount = averageWaterDay.toInt() // Correct way to assign value
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = waterPerMonthMin,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xFF1c37ff,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, bottom = 50.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFF1c37ff) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
@Composable
fun MoveMinutesScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {

    //data
    val moveMinutesWeek: List<Int> by viewModel.weeklyMoveMinutes.collectAsState()
    val moveMinutesWeekFloats = moveMinutesWeek.map { it.toFloat() }
    val moveMinutesMonth by viewModel.monthlyMoveMinutes.collectAsState()
    val moveMinutesMonthFloats = moveMinutesMonth.map { it.toFloat() }
    val averageMoveMinutesDay = moveMinutesWeek.filter { it > 0 }.average()
    val averageMoveMinutesMonthBox = moveMinutesMonth.filter { it > 0 }.average()

    val markerText = "Average Move Minutes:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Move Minutes Data",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626))
                .padding(start = 10.dp)
                .padding(paddingValues)
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average Move Minutes: ${String.format("%.0f", averageMoveMinutesDay)}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average Move Minutes: ${averageMoveMinutesMonthBox.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = moveMinutesWeekFloats,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xffff5500,
                            markerText = "Move Minutes:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = moveMinutesMonthFloats,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xffff5500,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xffff5500) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Move Minutes are measured of anything that gets you moving, helping you understand how active you are each day",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun DistanceScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {

    //data
    val distanceWeek by viewModel.weeklyDistance.collectAsState()
    val distanceMonth by viewModel.monthlyDistance.collectAsState()
    val averageDistanceDay = distanceWeek.filter { it > 0 }.average()
    val averageDistanceMonthBox = distanceMonth.filter { it > 0 }.average()

    val markerText = "Average Distance in km:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Distance Data",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626))
                .padding(start = 10.dp)
                .padding(paddingValues)
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average Distance: ${String.format("%.2f", averageDistanceDay/1000)} km",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average Distance: ${String.format("%.2f",averageDistanceMonthBox/1000)} km",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = distanceWeek,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xffff5500,
                            markerText = "Distance in km:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = distanceMonth,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xffff5500,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xffff5500) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Measuring your distance is a useful way to track your achievements in activaties like cycling, running or swimming",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun SpeedScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {

    //data
    val distanceWeek by viewModel.weeklyDistance.collectAsState()
    val distanceMonth by viewModel.monthlyDistance.collectAsState()
    val moveMinutesWeek: List<Int> by viewModel.weeklyMoveMinutes.collectAsState()
    val moveMinutesMonth by viewModel.monthlyMoveMinutes.collectAsState()

    val speedWeek = distanceWeek.zip(moveMinutesWeek) { distance, moveMinutes ->
        if (moveMinutes > 0) (distance / (moveMinutes * 60f)) * 3.6f else 0f
    }
    val speedMonth = distanceMonth.zip(moveMinutesMonth) { distance, moveMinutes ->
        if (moveMinutes > 0) {
            val speedInKmPerHour = (distance / 1000f) / (moveMinutes / 60f)
            speedInKmPerHour * 1f
        } else 0f
    }


    val averageSpeedDay = speedWeek.filter { it > 0 }.average()
    val averageSpeedMonthBox = speedMonth.filter { it > 0 }.average()

    val markerText = "Average Speed in km/h:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Distance Data",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626))
                .padding(start = 10.dp)
                .padding(paddingValues)
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average Speed: ${String.format("%.2f", averageSpeedDay)} km/h",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average Speed: ${String.format("%.2f",averageSpeedMonthBox)} km/h",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = speedWeek,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xffff5500,
                            markerText = "Average Speed km/h:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = speedMonth,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xffff5500,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xffff5500) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Speed is measured in km/h, and can help you see your progress in activaties, like cycling or running",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun NutritionCaloriesScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {

    //data
    val weeksNutrition by viewModel.weeklyNutritionCounts.collectAsState()
    val weeksNutritionFloats = weeksNutrition.map { it.toFloat() }

    val monthsNutrition by viewModel.monthlyNutritionCounts.collectAsState()
    val monthsNutritionFloats = monthsNutrition.map { it.toFloat() }

    val averageCalEatDay = weeksNutrition.filter { it > 0 }.average()
    val averageCalEatBox = monthsNutrition.filter { it > 0 }.average()

    val markerText = "Average Calories consumed:"

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Calories Consumed",
                onEditClick = { /* ... */ },
                showEditIcon = false ,
                showBackButton = true,
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626))
                .padding(start = 10.dp)
                .padding(paddingValues)
        ) {
            item {
                val context = LocalContext.current
                // State to trigger re-fetching data
                var fetchDataTrigger by remember { mutableStateOf(false) }
                // Example fetching function
                val fetchCaloriesData = remember {
                    {
                        viewModel.fetchTodaysNutrition()
                        viewModel.fetchWeeksNutrition()
                        viewModel.fetchMonthNutrition()
                        Log.d("MyHealthApp", "Data fetched")
                    }
                }
                LaunchedEffect(fetchDataTrigger) {
                    fetchCaloriesData()
                }
                CaloriesInput { calories ->
                    val currentTime = System.currentTimeMillis()
                    CoroutineScope(Dispatchers.IO).launch {
                        addCaloriesToGoogleFit(context, calories, currentTime, currentTime)
                        fetchDataTrigger = !fetchDataTrigger
                    }
                }
            }
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(modifier = Modifier
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average calories consumed: ${averageCalEatDay.toInt()}",
                                color = Color.White,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average calories consumed: ${averageCalEatBox.toInt()}",
                                color = Color.White,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = weeksNutritionFloats,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xFFA6DECD,
                            markerText = "Calories consumed:"
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = monthsNutritionFloats,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xFFA6DECD,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFFA6DECD) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Calories are an estimate of energy gained from food and drink",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}
@Composable
fun CaloriesInput(onAddCalories: (Float) -> Unit) {
    var calories by remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(20.dp))
    Box(modifier = Modifier
        .padding(top = 10.dp, end = 10.dp)
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth()
        .background(color = Color.DarkGray)
        .padding(20.dp)) {
        Column {
            Text(text = "Enter calories", color = Color.White, modifier = Modifier.fillMaxWidth(), fontSize = 20.sp, textAlign = TextAlign.Center)
            // Calories input field
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Calories") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 0.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Convert the input to Float and trigger the addition function
                    calories.toFloatOrNull()?.let { cal ->
                        onAddCalories(cal)
                        calories = "" // Reset input field after submission
                    }
                },
                modifier = Modifier
                    .padding(end = 0.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF08012e)),
                ) {
                Text("Add Calories", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
@Composable
internal fun Chart2(
    modifier: Modifier,
    stepData: List<Float>,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    scrollState: Boolean,
    color: Long,
    markerText: String
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    // Trigger recomposition and model update when stepData changes
    LaunchedEffect(stepData) {
        Log.d("Chart2", "Updating chart model with new step data.")
        modelProducer.tryRunTransaction {
            columnSeries {
                series(stepData)
            }
        }
    }
    ComposeChart2(modelProducer, modifier, axisFormatter, scrollState, color, stepData, markerText)
}

@Composable
private fun ComposeChart2(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    scrollState: Boolean,
    color: Long,
    stepData: List<Float>,
    markerText: String
) {
    val isSleepData = markerText.contains("sleep", ignoreCase = true)
    val distanceData = markerText.contains("distance", ignoreCase = true)
    val speedData = markerText.contains("speed", ignoreCase = true)
    val itemPlacer = if (isSleepData) {
        remember { AxisItemPlacer.Vertical.step({ _ -> 1f }, false) }
    } else if(speedData){
        remember { AxisItemPlacer.Vertical.step({ _ -> 1f }, false) }
    }else {
        remember { AxisItemPlacer.Vertical.step() }
    }
    val startAxisH = if(isSleepData) {
            AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
                "${value.toInt()}h"
            }
    } else if(distanceData){
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
            "${value.toInt()}m"
        }

    } else {
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
            "${value.toInt()}"
        }
    }

    CartesianChartHost(
        scrollState = rememberVicoScrollState(scrollEnabled = scrollState),
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                listOf(
                    rememberLineComponent(
                        color = Color(color),
                        thickness = 25.dp,
                        shape = Shapes.roundedCornerShape(allPercent = 40)
                    ),
                ),
            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(Color.White),
                axis = rememberAxisLineComponent(Color.White),
                guideline = rememberAxisGuidelineComponent(Color.White),
                itemPlacer = itemPlacer,
                valueFormatter = startAxisH,
            ),
            bottomAxis =
            rememberBottomAxis(
                label = rememberAxisLabelComponent(Color.White),
                axis = rememberAxisLineComponent(Color.White),
                guideline = rememberAxisGuidelineComponent(Color.White),
                valueFormatter = axisFormatter,
                tick = rememberAxisTickComponent(),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(data = stepData, markerText = markerText),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}