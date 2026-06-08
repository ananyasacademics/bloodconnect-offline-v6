package com.ananyasacademics.bloodconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.ananyasacademics.bloodconnect.ui.navigation.Routes
import com.ananyasacademics.bloodconnect.ui.screens.AddDonorScreen
import com.ananyasacademics.bloodconnect.ui.screens.DashboardScreen
import com.ananyasacademics.bloodconnect.ui.screens.DonorListScreen
import com.ananyasacademics.bloodconnect.ui.screens.EmergencyScreen
import com.ananyasacademics.bloodconnect.ui.screens.HomeScreen
import com.ananyasacademics.bloodconnect.ui.screens.PrivacyScreen
import com.ananyasacademics.bloodconnect.ui.theme.BloodConnectOfflineV6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloodConnectOfflineV6Theme {
                Surface(
                    color = Color(0xFFFFF8FA)
                ) {
                    var currentScreen by remember { mutableStateOf(Routes.HOME) }

                    when (currentScreen) {

                        Routes.HOME -> {
                            HomeScreen(
                                onAddDonorClick = {
                                    currentScreen = Routes.ADD_DONOR
                                },
                                onDonorListClick = {
                                    currentScreen = Routes.DONOR_LIST
                                },
                                onEmergencyClick = {
                                    currentScreen = Routes.EMERGENCY
                                },
                                onDashboardClick = {
                                    currentScreen = Routes.DASHBOARD
                                },
                                onPrivacyClick = {
                                    currentScreen = Routes.PRIVACY
                                }
                            )
                        }

                        Routes.ADD_DONOR -> {
                            AddDonorScreen(
                                onSaveClick = { _, _, _, _, _, _ ->
                                    currentScreen = Routes.HOME
                                },
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.DONOR_LIST -> {
                            DonorListScreen(
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.EMERGENCY -> {
                            EmergencyScreen(
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.DASHBOARD -> {
                            DashboardScreen(
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.PRIVACY -> {
                            PrivacyScreen(
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}