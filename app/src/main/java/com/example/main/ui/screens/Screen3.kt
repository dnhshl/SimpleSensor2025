package com.example.main.ui.screens

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.*
import co.yml.charts.common.extensions.formatToSinglePrecision
import com.example.main.model.BottomAxisLabelKey
import com.example.main.model.MAXPULS
import com.example.main.model.MAX_FITNESS_DATA
import com.example.main.model.MINPULS
import com.example.main.model.MainViewModel
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape

import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays.fill


@Composable
fun Screen3(viewModel: MainViewModel, navController: NavController) {

    DisposableEffect(Unit) {
        viewModel.startFetchingChartData(3000)
        onDispose { viewModel.stopFetchingChartData() }
    }

    val state by viewModel.state.collectAsState()
    val fitnessData = state.fitnessDataList
    val modelProducer = state.modelProducer




    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (fitnessData.isNotEmpty()) {
            //StraightLinechart(pointsData)
            //MyLinechart(fitnessData)
            //MyWavechart(fitnessData)
            JetpackComposeBasicLineChart(modelProducer)

        }
    }
}

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}


@Composable
private fun JetpackComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {


    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        pointProvider = LineCartesianLayer.PointProvider.single(
                            LineCartesianLayer.point(
                                rememberShapeComponent(
                                    fill = Fill.Black,
                                    shape = Shape.Rectangle
                                )
                            )
                        ),
                    )
                ),
                rangeProvider = remember {
                    CartesianLayerRangeProvider.fixed(
                        minY = 0.0,
                        maxY = 200.0
                    )
                }
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                labelRotationDegrees = 25f,
                valueFormatter = BottomAxisValueFormatter
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        animationSpec = null,
    )
}

