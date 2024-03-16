package com.example.bakis

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.bakis.screens.PieData
import kotlinx.coroutines.launch

data class ArcData(
    val animation: Animatable<Float, AnimationVector1D>,
    val targetSweepAngle: Float,
    val color: Color,
)

@Composable
fun AnimatedNPieChart(
    modifier: Modifier = Modifier,
    pieDataPoints: List<PieData>,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    val localModifier = modifier.size(200.dp)
    val total = pieDataPoints.fold(0f) { acc, pieData ->
        acc + pieData.value
    }.div(360)
    var currentSum = 0
    val arcs = pieDataPoints.map {
        currentSum += it.value
        ArcData(
            targetSweepAngle = currentSum / total,
            animation = Animatable(0f),
            color = it.color
        )
    }
    LaunchedEffect(arcs) {
        arcs.map {
            launch {
                it.animation.animateTo(
                    targetValue = it.targetSweepAngle,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }
    Canvas(modifier = localModifier.scale(1f)) {
        val stroke = Stroke(width = 20f)

        arcs.reversed().map {
            drawArc(
                startAngle = -90f,
                sweepAngle = it.animation.value,
                color = it.color,
                useCenter = false,
                style = stroke
            )
        }
    }
    content(modifier = localModifier.scale(1f))
}