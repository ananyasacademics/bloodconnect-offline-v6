package com.ananyasacademics.bloodconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.ananyasacademics.bloodconnect.data.local.BloodConnectDatabase
import com.ananyasacademics.bloodconnect.data.model.Donor
import com.ananyasacademics.bloodconnect.data.repository.DonorRepository
import com.ananyasacademics.bloodconnect.ui.navigation.Routes
import com.ananyasacademics.bloodconnect.ui.screens.AddDonorScreen
import com.ananyasacademics.bloodconnect.ui.screens.DashboardScreen
import com.ananyasacademics.bloodconnect.ui.screens.DonorListScreen
import com.ananyasacademics.bloodconnect.ui.screens.EmergencyScreen
import com.ananyasacademics.bloodconnect.ui.screens.HomeScreen
import com.ananyasacademics.bloodconnect.ui.screens.PrivacyScreen
import com.ananyasacademics.bloodconnect.ui.theme.BloodConnectOfflineV6Theme
import com.ananyasacademics.bloodconnect.viewmodel.DonorViewModel
import com.ananyasacademics.bloodconnect.viewmodel.DonorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = BloodConnectDatabase.getDatabase(applicationContext)
        val repository = DonorRepository(database.donorDao())
        val donorViewModelFactory = DonorViewModelFactory(repository)

        setContent {
            BloodConnectOfflineV6Theme {
                Surface(
                    color = Color(0xFFFFF8FA)
                ) {
                    val donorViewModel: DonorViewModel = viewModel(
                        factory = donorViewModelFactory
                    )

                    val donors by donorViewModel.allDonors.collectAsState(
                        initial = emptyList()
                    )

                    var currentScreen by remember {
                        mutableStateOf(Routes.HOME)
                    }

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
                                onSaveClick = { name, bloodGroup, phone, area, available, notes ->
                                    donorViewModel.addDonor(
                                        Donor(
                                            name = name,
                                            bloodGroup = bloodGroup,
                                            phone = phone,
                                            area = area,
                                            availabilityStatus = if (available) "Available" else "Unavailable",
                                            notes = notes
                                        )
                                    )

                                    currentScreen = Routes.HOME
                                },
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.DONOR_LIST -> {
                            DonorListScreen(
                                donors = donors,
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