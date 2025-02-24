package com.example.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.main.model.MAXPULS
import com.example.main.model.MINPULS
import com.example.main.model.MainViewModel


@Composable
fun Screen3(viewModel: MainViewModel, navController: NavController) {

    DisposableEffect(Unit) {
        viewModel.startFetchingChartData(3000)
        onDispose { viewModel.stopFetchingChartData() }
    }

    //val state by viewModel.state.collectAsState()
    //val modelProducer = state.modelProducer

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicLineChart(viewModel.modelProducer, minY = MINPULS, maxY = MAXPULS)
    }
}



