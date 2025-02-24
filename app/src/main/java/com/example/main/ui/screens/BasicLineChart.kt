package com.example.main.ui.screens

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.Shape

val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

@Composable
fun BasicLineChart(
    modelProducer: CartesianChartModelProducer,
    minY: Double = 0.0,
    maxY: Double = 200.0,
    modifier: Modifier = Modifier,
) {
    // Konfiguration des Charts
    val chart = rememberCartesianChart(
        rememberLineCartesianLayer(
            lineProvider = LineCartesianLayer.LineProvider.series(
                LineCartesianLayer.rememberLine(
                    pointProvider = LineCartesianLayer.PointProvider.single(
                        LineCartesianLayer.point(
                            rememberShapeComponent(
                                fill = Fill(Color.BLUE),
                                shape = Shape.Rectangle
                            )
                        )
                    ),
                )
            ),
            rangeProvider = remember {
                CartesianLayerRangeProvider.fixed(
                    minY = minY,
                    maxY = maxY
                )
            }
        ),
        startAxis = VerticalAxis.rememberStart(),
        bottomAxis = HorizontalAxis.rememberBottom(
            labelRotationDegrees = 25f,
            valueFormatter = { context, x, _ ->
                context.model.extraStore[BottomAxisLabelKey]
                    ?.getOrNull(x.toInt())
                    ?: " "
            }
        )
    )

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier,
        animationSpec = null
    )
}

