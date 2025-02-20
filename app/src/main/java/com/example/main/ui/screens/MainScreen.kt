package com.example.main.ui.screens

import android.hardware.Sensor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.main.model.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    val sensors = state.sensors

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()

    ) {
        items(sensors) {sensor -> ListItemCard(sensor) }
    }
}


@Composable
fun ListItemCard(
    sensor: Sensor,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = sensor.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "vendor: " + sensor.vendor, style = MaterialTheme.typography.bodySmall)
            Text(text = "version: " + sensor.version, style = MaterialTheme.typography.bodySmall)
            Text(text = "type: " + sensor.type, style = MaterialTheme.typography.bodySmall)
            Text(text = "maxRange: " + sensor.maximumRange, style = MaterialTheme.typography.bodySmall)
            Text(text = "resolution: " + sensor.resolution, style = MaterialTheme.typography.bodySmall)
            Text(text = "power: " + sensor.power, style = MaterialTheme.typography.bodySmall)
            Text(text = "minDelay: " + sensor.minDelay, style = MaterialTheme.typography.bodySmall)
        }
    }
}