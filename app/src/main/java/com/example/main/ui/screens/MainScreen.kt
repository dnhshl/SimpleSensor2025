package com.example.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    val fitnessData = state.fitnessData

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        val displayText = if (fitnessData != null) {
            stringResource(id = R.string.FitnessDataText, fitnessData.fitness,
                fitnessData.puls, fitnessData.formattedTimestamp)
        } else {
            stringResource(id = R.string.NoData)
        }
        Text(
            text = displayText,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.fetchJsonData() }) {
            Text("JSON Daten abrufen")
        }
    }
}


