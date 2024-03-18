package com.example.bakis.graphscreen

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
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
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import java.util.Calendar

//data example Steps
val stepsPerDay = listOf(3000f, 5000f, 4500f, 2000f, 5500f, 7000f, 4000f)
val stepsPerMonth = listOf(3000f, 5000f, 4500f, 2000f, 5500f, 7000f, 4000f, 3000f, 5000f, 4500f, 2000f, 5500f)
val averageStepsDay = stepsPerDay.average()
val averageStepsMonth = stepsPerMonth.average()
var averageStepsCount = averageStepsMonth.toInt()
//data example Sleep
val sleepPerDayMin = listOf(300f, 800f, 900f, 500f, 550f, 700f, 400f)
val sleepPerMonthMin = listOf(300f, 500f, 400f, 200f, 500f, 700f, 400f, 300f, 500f, 450f, 200f, 2000f)
val averageSleepDay = sleepPerDayMin.average()
val averageSleepMonth = sleepPerMonthMin.average()
var averageSleepCount = averageSleepMonth.toInt()
//data example Calories
val caloriesPerDayMin = listOf(1700f, 1863f, 1230f, 2023f, 1475f, 700f, 1900f)
val caloriesPerMonthMin = listOf(1652f, 2530f, 1432f, 1896f, 1700f, 1863f, 1230f, 2023f, 1475f, 700f, 1900f, 1420f)
val averageCaloriesDay = caloriesPerDayMin.average()
val averageCaloriesMonth = caloriesPerMonthMin.average()
var averageCaloriesCount = averageCaloriesMonth.toInt()



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
                .padding(top = 40.dp, start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                if(selectedLabel.value == "Week")
                    averageStepsCount = averageStepsDay.toInt()

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text="Average Steps: $averageStepsCount",
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
                            stepData = stepsPerDay,
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
                }
            }
        }
    }
}
//************SLEEP DATA**************************
@Composable
fun SleepScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
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
                .padding(top = 40.dp, start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                if(selectedLabel.value == "Week")
                    averageSleepCount = averageSleepDay.toInt()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text="Average Sleep: $averageSleepCount",
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
                            stepData = sleepPerDayMin,
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
                }
            }
        }
    }
}
@Composable
fun CaloriesScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
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
                if(selectedLabel.value == "Week")
                    averageCaloriesCount = averageCaloriesDay.toInt()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text="Average Sleep: $averageCaloriesCount",
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
                }
            }
        }
    }
}
@Composable
fun WaterIntakeScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val userId by viewModel.userId.collectAsState()
    val dailyWaterIntakeTotals = viewModel.getDailyWaterIntakeTotalsForUser(userId)
        .collectAsState(initial = emptyList()).value
    val monthlyWaterIntakeTotals = viewModel.getMonthlyWaterIntakeTotalsForUser(userId)
        .collectAsState(initial = emptyList()).value
    val waterPerMonthMin = monthlyWaterIntakeTotals.map { it.second.toFloat() }
    val waterPerDayMin = dailyWaterIntakeTotals.map { it.second.toFloat() }

    // Determine if data is still loading
    val isLoading = dailyWaterIntakeTotals.isEmpty() && monthlyWaterIntakeTotals.isEmpty()

    if (isLoading) {
        // Show a loading spinner or some placeholder
        CircularProgressIndicator()
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
                            text="Average Water Intake: $averageWaterCount",
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
//chart data
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



    // Your ComposeChart2 call remains the same
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
        scrollState =rememberVicoScrollState(scrollEnabled = scrollState),
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
            decorations = listOf(rememberComposeThresholdLine(scrollState)),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}
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
