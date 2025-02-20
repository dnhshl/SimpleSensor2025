package com.example.main.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.main.model.MainViewModel
import com.example.main.ui.screens.MainScreen
import com.example.main.ui.screens.MyScreens
import com.example.main.ui.screens.Screen2
import com.example.main.ui.screens.Screen3

@Composable
fun MyNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Screens via BottomBar und Fullscreens
        composable(MyScreens.Main.route) { MainScreen(viewModel, navController) }
        composable(MyScreens.Screen2.route) { Screen2(viewModel, navController) }
        composable(MyScreens.Screen3.route) { Screen3(viewModel, navController) }

    }
}
