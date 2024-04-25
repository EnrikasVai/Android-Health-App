package com.example.bakis.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import java.util.Calendar

fun calculateWeekDays(): List<String> {
    val daysOfWeek = listOf("S","M", "T", "W", "T", "F", "S")
    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_WEEK) - 1
    return (0 until 7).map { i ->
        daysOfWeek[(today - i + 7) % 7]
    }.reversed()
}
val daysOfWeek = calculateWeekDays()
private val bottomAxisValueFormatter =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        daysOfWeek[x.toInt() % daysOfWeek.size]
    }
@Composable
fun HealthBox(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    detail: String,
    detailSub: String,
    caloriesPerDay: List<Float>,
    navController: NavController,
    navigateTo: String,
    color: Long,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF333333))
            .clickable { navController.navigate(navigateTo) }
            .padding(15.dp)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                Text(text = title, fontSize = 20.sp, color = Color.White)
                Text(text = subtitle, fontSize = 13.sp, color = Color.White)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(text = detail, color = Color(color), fontSize = 20.sp)
                    Text(text = detailSub, color = Color(color), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Chart3(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .width(250.dp),
                    stepData = caloriesPerDay,
                    axisFormatter = bottomAxisValueFormatter,
                    color = color
                )
            }
        }
    }
}
@Composable
internal fun Chart3(
    modifier: Modifier,
    stepData: List<Float>,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    color: Long
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(stepData) {
        Log.d("Chart3", "Updating chart model with new step data.")
        modelProducer.tryRunTransaction {
            columnSeries {
                series(stepData)
            }
        }
    }
    ComposeChart3(modelProducer, modifier, axisFormatter,color)
}

@Composable
private fun ComposeChart3(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    color: Long
) {
    CartesianChartHost(
        scrollState = rememberVicoScrollState(false),
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
                label = rememberAxisLabelComponent(Color(0xFF333333)),
                axis = rememberAxisLineComponent(Color(0xFF333333)),
                guideline = rememberAxisGuidelineComponent(Color(0xFF333333)),
                tick = rememberAxisTickComponent(thickness = 0.dp)
            ),
            bottomAxis =
            rememberBottomAxis(
                label = rememberAxisLabelComponent(Color.White),
                axis = rememberAxisLineComponent(Color(0xFF333333)),
                guideline = rememberAxisGuidelineComponent(Color(0xFF333333)),
                valueFormatter = axisFormatter,
                tick = rememberAxisTickComponent(thickness = 0.dp),
                itemPlacer =
                remember { AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true) },
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        horizontalLayout = HorizontalLayout.fullWidth(),
    )
}