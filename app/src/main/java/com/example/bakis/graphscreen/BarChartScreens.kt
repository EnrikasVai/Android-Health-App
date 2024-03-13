package com.example.bakis.graphscreen

import android.graphics.Typeface
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

//data example
val stepsPerDay = listOf(3000f, 5000f, 4500f, 2000f, 5500f, 7000f, 4000f)
val stepsPerMonth = listOf(3000f, 5000f, 4500f, 2000f, 5500f, 7000f, 4000f, 3000f, 5000f, 4500f, 2000f, 5500f)
val averageStepsDay = stepsPerDay.average()
val averageStepsMonth = stepsPerMonth.average()
var averageStepsCount = averageStepsMonth.toInt()

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
                icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
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
                        .clip(shape = RoundedCornerShape(30.dp))
                        .padding(10.dp)
                        .background(color = Color.DarkGray)
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
                                .height(350.dp)
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart3(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp)
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))) {
                        labels.forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Equal weight to distribute spaceDarkGray
                                    .background(if (label == selectedLabel.value) Color.LightGray else Color.DarkGray)
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
internal fun Chart2(
    modifier: Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        series(stepsPerDay)
                    }
                }
            }
        }
    }
    ComposeChart2(modelProducer, modifier)
}
@Composable
private fun ComposeChart2(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
) {
    CartesianChartHost(
        scrollState =rememberVicoScrollState(scrollEnabled = false),
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                listOf(
                    rememberLineComponent(
                        color = Color(0xffff5500),
                        thickness = 65.dp,
                        shape = Shapes.roundedCornerShape(allPercent = 40),

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
                valueFormatter = bottomAxisValueFormatter,
                tick = rememberAxisTickComponent(),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
            decorations = listOf(rememberComposeThresholdLine()),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}
@Composable
internal fun Chart3(
    modifier: Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        series(stepsPerMonth)
                    }
                }
            }
        }
    }
    ComposeChart3(modelProducer, modifier)
}
@Composable
private fun ComposeChart3(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
) {
    CartesianChartHost(
        //scrollState =rememberVicoScrollState(scrollEnabled = false),
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                listOf(
                    rememberLineComponent(
                        color = Color(0xffff5500),
                        thickness = 30.dp,
                        shape = Shapes.roundedCornerShape(allPercent = 40),

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
                valueFormatter = bottomAxisValueFormatterMonth,
                tick = rememberAxisTickComponent(),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
            decorations = listOf(rememberComposeThresholdLine1()),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}
@Composable
private fun rememberComposeThresholdLine(): ThresholdLine {
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
        ThresholdLine(thresholdValue = THRESHOLD_LINE_Y.toFloat(), lineComponent = line, labelComponent = label)
    }
}
@Composable
private fun rememberComposeThresholdLine1(): ThresholdLine {
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
        ThresholdLine(thresholdValue = THRESHOLD_LINE_Y1.toFloat(), lineComponent = line, labelComponent = label)
    }
}
private val THRESHOLD_LINE_Y1 = averageStepsMonth
private val THRESHOLD_LINE_Y = averageStepsDay
private const val THRESHOLD_LINE_COLOR = -2893786
private const val THRESHOLD_LINE_LABEL_HORIZONTAL_PADDING_DP = 8f
private const val THRESHOLD_LINE_LABEL_VERTICAL_PADDING_DP = 2f
private const val THRESHOLD_LINE_LABEL_MARGIN_DP = 4f

//private val monthNames = DateFormatSymbols.getInstance(Locale.US).shortMonths
//private val bottomAxisValueFormatter =
//AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
//   "${monthNames[x.toInt() % 12]} ’${20 + x.toInt() / 12}"
// }
val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val bottomAxisValueFormatter =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use daysOfWeek to get the label for each x value
        daysOfWeek[x.toInt() % daysOfWeek.size]
    }
val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug","Sep","Oct", "Nov", "Dec")
private val bottomAxisValueFormatterMonth =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use daysOfWeek to get the label for each x value
        months[x.toInt() % months.size]
    }
//************SLEEP DATA**************************
//data example
val sleepPerDayMin = listOf(300f, 800f, 900f, 500f, 550f, 700f, 400f)
val sleepPerMonthMin = listOf(300f, 500f, 400f, 200f, 500f, 700f, 400f, 300f, 500f, 450f, 200f, 2000f)
val averageSleepDay = sleepPerDayMin.average()
val averageSleepMonth = sleepPerMonthMin.average()
var averageSleepCount = averageSleepMonth.toInt()
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
                icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
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
                        .clip(shape = RoundedCornerShape(30.dp))
                        .padding(10.dp)
                        .background(color = Color.DarkGray)
                    ) {
                        Text(
                            text="Average Sleep: $averageSleepCount",
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    if(selectedLabel.value == "Week") {
                        ChartSleep1(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp)
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        ChartSleep2(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp)
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))) {
                        labels.forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Equal weight to distribute spaceDarkGray
                                    .background(if (label == selectedLabel.value) Color.LightGray else Color.DarkGray)
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
internal fun ChartSleep1(
    modifier: Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        series(sleepPerDayMin)
                    }
                }
            }
        }
    }
    ComposeChartSleep1(modelProducer, modifier)
}
@Composable
private fun ComposeChartSleep1(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
) {
    CartesianChartHost(
        scrollState =rememberVicoScrollState(scrollEnabled = false),
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                listOf(
                    rememberLineComponent(
                        color = Color(0xFF09bfe8),
                        thickness = 65.dp,
                        shape = Shapes.roundedCornerShape(allPercent = 40),

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
                valueFormatter = bottomAxisValueFormatter,
                tick = rememberAxisTickComponent(),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
            decorations = listOf(rememberComposeThresholdLine()),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}
@Composable
internal fun ChartSleep2(
    modifier: Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    columnSeries {
                        series(sleepPerMonthMin)
                    }
                }
            }
        }
    }
    ComposeChartSleep2(modelProducer, modifier)
}
@Composable
private fun ComposeChartSleep2(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
) {
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                listOf(
                    rememberLineComponent(
                        color = Color(0xFF09bfe8),
                        thickness = 30.dp,
                        shape = Shapes.roundedCornerShape(allPercent = 40),

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
                valueFormatter = bottomAxisValueFormatterMonth,
                tick = rememberAxisTickComponent(),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
            decorations = listOf(rememberComposeThresholdLine1()),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = rememberMarker(),
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}









/* YCHARTS BLOGAI ATVAIZDUOJA
/*
@Composable
fun BarChart() {
    val barChartData = listOf(3000f, 5000f, 2500f, 4000f, 1500f, 5500f, 2000f) // Your data goes here
    val maxValue = barChartData.maxOrNull() ?: 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            barChartData.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * size.height
                drawRoundRect(
                    color = Color.Red,
                    topLeft = Offset(
                        x = (index * (size.width / barChartData.size)).toFloat(),
                        y = size.height - barHeight
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = (size.width / barChartData.size) * 0.5f,
                        height = barHeight
                    ),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun BarchartWithBackgroundColor() {

    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val steps = listOf(3000, 5000, 7000, 8000, 10000, 12000, 2000)

    val barData = weekdays.zip(steps).mapIndexed { index, (day, stepCount) ->
        BarData(
            point = Point(x = index.toFloat(), y = stepCount.toFloat()),
            label = day,
            color = Color(0xFFFF7518)
        )
    }
    val maxStepValue = steps.maxOrNull() ?: 0
    val maxRange = maxStepValue + 1000
    val backgroundColor = Color(0xFF262626)
    val yStepSize = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(60.dp)
        .axisLineColor(Color.White)
        .axisLabelColor(Color.White)
        .steps(barData.size - 1)
        .bottomPadding(10.dp)
        .startDrawPadding(30.dp)
        .axisLabelAngle(0f)
        .labelData { index -> barData[index].label }
        .backgroundColor(backgroundColor)
        .build()
    val yAxisData = AxisData.Builder()
        .axisLineColor(Color.White)
        .axisLabelColor(Color.White)
        .steps(yStepSize)
        .labelAndAxisLinePadding(35.dp)
        .axisOffset(20.dp)
        .backgroundColor(backgroundColor)
        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
        .build()
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(paddingBetweenBars = 15.dp,
            barWidth = 30.dp,
            selectionHighlightData = SelectionHighlightData(
                highlightBarColor = Color.Red,
                highlightTextBackgroundColor = Color.Green,
                popUpLabel = { _, y -> " Value : $y " }
            )),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 0.dp,
        backgroundColor = backgroundColor
    )
    BarChart(modifier = Modifier
        .height(300.dp),
        barChartData = barChartData)
}

 */