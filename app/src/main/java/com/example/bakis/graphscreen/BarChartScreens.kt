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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.composables.StepProgressBar
import com.example.bakis.composables.WaterIntakeTracker
import com.example.bakis.rememberMarker
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
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import kotlinx.coroutines.delay
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

// Use the new function names here
val daysOfWeek = calculateWeekDays()
private val bottomAxisValueFormatter =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use daysOfWeek to get the label for each x value
        daysOfWeek[x.toInt() % daysOfWeek.size]
    }

val months = calculateMonths()
private val bottomAxisValueFormatterMonth =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use months to get the label for each x value
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

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Steps Data",
                onEditClick = { /* ... */ },
                showEditIcon = false // Only show the edit icon in the ProfileScreen
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
                            text="Average Steps: ${averageStepsDay.toInt()}",
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
                            color = 0xffff5500
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
                            color = 0xffff5500
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

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Sleep Data",
                onEditClick = { /* ... */ },
                showEditIcon = false // Only show the edit icon in the ProfileScreen
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
                            stepData = sleepPerDayFloats,
                            axisFormatter = bottomAxisValueFormatter,
                            scrollState = false,
                            color = 0xFF09bfe8
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            stepData = sleepPerMonthMin,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            scrollState = true,
                            color = 0xFF09bfe8
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
                showEditIcon = false // Only show the edit icon in the ProfileScreen
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
                .padding(top = 40.dp, start = 10.dp)
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
                                text = "Average Calories: ${averageCaloriesDay.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        } else {
                            Text(
                                text = "Average Calories: ${averageCaloriesMonth.toInt()}",
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
                            color = 0xFFf52749
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
                            color = 0xFFf52749
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
                showEditIcon = false // Only show the edit icon in the ProfileScreen
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
                            color = 0xFF1c37ff
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
                            color = 0xFF1c37ff
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
internal fun Chart2(
    modifier: Modifier,
    stepData: List<Float>,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    scrollState: Boolean,
    color: Long
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
    ComposeChart2(modelProducer, modifier, axisFormatter, scrollState, color)
}

@Composable
private fun ComposeChart2(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    scrollState: Boolean,
    color: Long
) {
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
                guideline = rememberAxisGuidelineComponent(Color.White)
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
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}
/*
@Composable
private fun rememberComposeThresholdLine(
    thresholdLineForDisplay: Boolean
): ThresholdLine {
    val color = Color(THRESHOLD_LINE_COLOR)
    val line = rememberShapeComponent(color = color)
    val label =
        rememberTextComponent(
            background = rememberShapeComponent(Shapes.pillShape, color),
            padding =
            dimensionsOf(
                THRESHOLD_LINE_LABEL_HORIZONTAL_PADDING_DP.dp,
                THRESHOLD_LINE_LABEL_VERTICAL_PADDING_DP.dp,
            ),
            margins = dimensionsOf(THRESHOLD_LINE_LABEL_MARGIN_DP.dp),
            typeface = Typeface.MONOSPACE,
        )
    return remember(line, label) {
        if (!thresholdLineForDisplay)
            ThresholdLine(thresholdValue = THRESHOLD_LINE_Y.toFloat(), lineComponent = line, labelComponent = label)
        else
            ThresholdLine(thresholdValue = THRESHOLD_LINE_Y1.toFloat(), lineComponent = line, labelComponent = label)
    }
}
private val THRESHOLD_LINE_Y1 = averageStepsMonth
private val THRESHOLD_LINE_Y = averageStepsDay
private const val THRESHOLD_LINE_COLOR = -2893786
private const val THRESHOLD_LINE_LABEL_HORIZONTAL_PADDING_DP = 8f
private const val THRESHOLD_LINE_LABEL_VERTICAL_PADDING_DP = 2f
private const val THRESHOLD_LINE_LABEL_MARGIN_DP = 4f

*/