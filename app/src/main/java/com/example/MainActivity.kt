package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TerropingViewModel
import com.example.ui.screens.PreventionScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SeismographScreen
import com.example.ui.screens.SismosScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TechBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: TerropingViewModel = viewModel()
                var activeTab by remember { mutableStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = SlateSurface,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Sismos"
                                    )
                                },
                                label = { Text("Sismos") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TechBlue,
                                    selectedTextColor = TechBlue,
                                    indicatorColor = SlateSurface
                                ),
                                modifier = Modifier.testTag("nav_tab_sismos")
                            )

                            NavigationBarItem(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sismógrafo"
                                    )
                                },
                                label = { Text("Sismógrafo") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TechBlue,
                                    selectedTextColor = TechBlue,
                                    indicatorColor = SlateSurface
                                ),
                                modifier = Modifier.testTag("nav_tab_seismograph")
                            )

                            NavigationBarItem(
                                selected = activeTab == 2,
                                onClick = { activeTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Reportar"
                                    )
                                },
                                label = { Text("Reportar") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TechBlue,
                                    selectedTextColor = TechBlue,
                                    indicatorColor = SlateSurface
                                ),
                                modifier = Modifier.testTag("nav_tab_reportar")
                            )

                            NavigationBarItem(
                                selected = activeTab == 3,
                                onClick = { activeTab = 3 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Prevención"
                                    )
                                },
                                label = { Text("Prevención") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TechBlue,
                                    selectedTextColor = TechBlue,
                                    indicatorColor = SlateSurface
                                ),
                                modifier = Modifier.testTag("nav_tab_prevention")
                            )
                        }
                    }
                ) { innerPadding ->
                    val contentModifier = Modifier.padding(innerPadding)
                    when (activeTab) {
                        0 -> SismosScreen(viewModel = viewModel, modifier = contentModifier)
                        1 -> SeismographScreen(viewModel = viewModel, modifier = contentModifier)
                        2 -> ReportsScreen(viewModel = viewModel, modifier = contentModifier)
                        3 -> PreventionScreen(modifier = contentModifier)
                    }
                }
            }
        }
    }
}
