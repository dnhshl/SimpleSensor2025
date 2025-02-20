package com.example.main.ui.screens


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.main.R


// hier "Verwaltungsinfo" zu allen Bildschirmen listen
// ----------------------------------------------------------------

sealed class MyScreens(
    val route: String,
    val titleID: Int = R.string.emptyString,
    val labelID: Int = R.string.emptyString,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val showBackArrow: Boolean = false
) {

    // BottomNavScreens benötigen Title, Label, Icons
    // ----------------------------------------------------------------

    object Main : MyScreens(
        route = "main",                          // eindeutige Kennung
        titleID = R.string.mainScreenTitle,      // Titel in der TopBar
        labelID = R.string.mainScreenLabel,      // Label in der BottomBar
        selectedIcon = Icons.Filled.Sensors,        // Icon in der BottomBar, wenn gewählt
        unselectedIcon =Icons.Outlined.Sensors,     // Icon in der BottomBar, wenn nicht gewählt
    )

    object Screen2 : MyScreens(
        route = "screen2",
        titleID = R.string.screen2Title,
        labelID = R.string.screen2Label,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
    )

    object Screen3 : MyScreens(
        route = "screen3",
        titleID = R.string.screen3Title,
        labelID = R.string.screen3Label,
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map,
    )




    companion object {
        val allScreens =
            listOf(Main, Screen2, Screen3)

        val bottomBarScreens = listOf(Main, Screen2, Screen3)

        fun fromRoute(route: String): MyScreens? =
            allScreens.firstOrNull { it.route == route }
    }

}



