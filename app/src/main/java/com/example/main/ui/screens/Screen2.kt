package com.example.main.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.MainViewModel


@Composable
fun Screen2(viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    val gyro = state.gyro
    val acceleration = state.acceleration
    val backgroundColor = state.backgroundColor

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gyro.size == 3) {
            Text("Gyro X: ${"%.2f".format(gyro[0])}", fontSize = 24.sp)
            Text("Gyro Y: ${"%.2f".format(gyro[1])}", fontSize = 24.sp)
            Text("Gyro Z: ${"%.2f".format(gyro[2])}", fontSize = 24.sp)
        } else {
            Text(stringResource(R.string.noGyroData), fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (acceleration.size == 3) {
            Text("Acceleration X: ${"%.2f".format(acceleration[0])}", fontSize = 24.sp)
            Text("Acceleration Y: ${"%.2f".format(acceleration[1])}", fontSize = 24.sp)
            Text("Acceleration Z: ${"%.2f".format(acceleration[2])}", fontSize = 24.sp)
        } else {
            Text(stringResource(R.string.noAccelerationData), fontSize = 24.sp)
        }

    }
}

