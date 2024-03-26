package com.example.bakis.graphscreen

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bakis.viewmodel.HomeViewModel
import com.patrykandpatrick.vico.compose.component.fixed
import com.patrykandpatrick.vico.compose.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.dashedShape
import com.patrykandpatrick.vico.compose.component.shape.markerCorneredShape
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter


@Composable
internal fun rememberMarker(
    labelPosition: MarkerComponent.LabelPosition = MarkerComponent.LabelPosition.Top,
    data: List<Float>,
    markerText: String
): Marker {

    val customLabelFormatter = CustomMarkerLabelFormatter(data, markerText)

    val labelBackgroundShape = Shapes.markerCorneredShape(Corner.FullyRounded)
    val labelBackground =
        rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.surface)
            .setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP,
                applyElevationOverlay = true,
            )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            background = labelBackground,
            padding = dimensionsOf(8.dp, 4.dp),
            typeface = Typeface.MONOSPACE,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent = rememberShapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
    val indicatorCenterComponent = rememberShapeComponent(Shapes.pillShape)
    val indicatorRearComponent = rememberShapeComponent(Shapes.pillShape)
    val indicator =
        rememberLayeredComponent(
            rear = indicatorRearComponent,
            front =
            rememberLayeredComponent(
                rear = indicatorCenterComponent,
                front = indicatorFrontComponent,
                padding = dimensionsOf(5.dp),
            ),
            padding = dimensionsOf(10.dp),
        )
    val guideline =
        rememberLineComponent(
            color = MaterialTheme.colorScheme.onSurface.copy(.2f),
            thickness = 2.dp,
            shape = Shapes.dashedShape(shape = Shapes.pillShape, dashLength = 8.dp, gapLength = 4.dp),
        )
    return remember(label, labelPosition, indicator, guideline, customLabelFormatter) {
        @SuppressLint("RestrictedApi")
        object : MarkerComponent(label, labelPosition, indicator, guideline) {
            init {
                labelFormatter = customLabelFormatter
                indicatorSizeDp = 36f
                onApplyEntryColor = { entryColor ->
                    indicatorRearComponent.color = entryColor.copyColor(alpha = .15f)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(radius = 12f, color = entryColor)
                    }
                }
            }

            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) {
                with(context) {
                    outInsets.top =
                        (
                                CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP -
                                        LABEL_BACKGROUND_SHADOW_DY_DP
                                )
                            .pixels
                    if (labelPosition == LabelPosition.AroundPoint) return
                    outInsets.top += label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels
                }
            }
        }
    }
}

class CustomMarkerLabelFormatter(
    private val dataset: List<Float>,
    private val markerText: String
) : MarkerLabelFormatter {
    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues
    ): CharSequence {
        val isSleepData = markerText.contains("sleep", ignoreCase = true)
        val labelBuilder = StringBuilder()
        for (entry in markedEntries) {
            val index = entry.index
            if (isSleepData) {
                val hoursFloat = dataset[index]
                val hours = hoursFloat.toInt()
                val minutes = ((hoursFloat - hours) * 60).toInt()
                val formattedTime = String.format("%dh %02dm", hours, minutes)
                labelBuilder.append("$markerText ").append(formattedTime).append("\n")
            } else {
                val displayValue = String.format("%.1f", dataset[index])
                labelBuilder.append("$markerText ").append(displayValue).append("\n")
            }
        }
        if (labelBuilder.isNotEmpty()) {
            labelBuilder.setLength(labelBuilder.length - 1)
        }
        return labelBuilder.toString()
    }
}






private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f