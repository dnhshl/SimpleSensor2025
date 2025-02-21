package com.example.main.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.MainViewModel
import com.github.anastr.speedometer.SpeedView
import com.github.anastr.speedometer.SpeedometerDefaults
import com.github.anastr.speedometer.components.Section

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@Composable
fun Screen2(viewModel: MainViewModel, navController: NavController) {

    DisposableEffect(Unit) {
        viewModel.startFetchingData(3000)
        onDispose { viewModel.stopFetchingData() }
    }

    val state by viewModel.state.collectAsState()
    val fitnessData = state.fitnessData

    // Animation für den Puls
    val animatedPulse by animateFloatAsState(
        targetValue = fitnessData?.puls?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
    )

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Puls",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        // Speedometer zeigt den animierten Pulse an
        SpeedView(
            modifier = Modifier.size(250.dp),
            speed = animatedPulse,
            minSpeed = 0f,
            maxSpeed = 200f,
            unit = "",
            sections = persistentListOf<Section>(
                Section(0f, .4f, Color.Green),
                Section(.4f,.7f, Color.Yellow),
                Section(.7f, 1f, Color.Red)),
            marksCount = 19,
            ticks = persistentListOf(.2f, .4f, .6f, .8f),
        )
    }
}
