package com.example.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.model.MainViewModel


@Composable
fun Screen3(viewModel: MainViewModel, navController: NavController) {

    DisposableEffect(Unit) {
        viewModel.startLocationUpdates()
        onDispose { viewModel.stopLocationUpdates() }
    }

    val state by viewModel.state.collectAsState()
    val location = state.location

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (location != null) {
            Text("Latitude: ${"%.4f".format(location.latitude)}", fontSize = 24.sp)
            Text("Longitude: ${"%.4f".format(location.longitude)}", fontSize = 24.sp)
            Text("Altitude: ${"%.4f".format(location.altitude)}", fontSize = 24.sp)
            Text("Accuracy: ${"%.4f".format(location.accuracy)}", fontSize = 24.sp)
        } else {
            Text("Location data not available", fontSize = 24.sp)
        }
    }
}
