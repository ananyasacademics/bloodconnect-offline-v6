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
import com.ananyasacademics.bloodconnect.ui.screens.CsvToolsScreen
import com.ananyasacademics.bloodconnect.ui.screens.DashboardScreen
import com.ananyasacademics.bloodconnect.ui.screens.DonorListScreen
import com.ananyasacademics.bloodconnect.ui.screens.EditDonorScreen
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

                    var selectedDonor by remember {
                        mutableStateOf<Donor?>(null)
                    }

                    var addDonorMessage by remember {
                        mutableStateOf("")
                    }

                    var showAddDonorDialog by remember {
                        mutableStateOf(false)
                    }

                    when (currentScreen) {

                        Routes.HOME -> {
                            HomeScreen(
                                onAddDonorClick = {
                                    addDonorMessage = ""
                                    showAddDonorDialog = false
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
                                onCsvToolsClick = {
                                    currentScreen = Routes.CSV_TOOLS
                                },
                                onPrivacyClick = {
                                    currentScreen = Routes.PRIVACY
                                }
                            )
                        }

                        Routes.ADD_DONOR -> {
                            AddDonorScreen(
                                message = addDonorMessage,
                                showMessageDialog = showAddDonorDialog,
                                onDismissMessage = {
                                    showAddDonorDialog = false
                                },
                                onSaveClick = { name, bloodGroup, phone, area, available, notes ->
                                    val cleanPhone = phone.filter { char -> char.isDigit() }
                                    val cleanName = name.trim().lowercase()
                                    val cleanArea = area.trim().lowercase()
                                    val cleanBloodGroup = bloodGroup.trim().uppercase()

                                    val duplicatePhone = donors.any {
                                        it.phone.filter { char -> char.isDigit() } == cleanPhone
                                    }

                                    val duplicateNameAreaBloodGroup = donors.any {
                                        it.name.trim().lowercase() == cleanName &&
                                                it.area.trim().lowercase() == cleanArea &&
                                                it.bloodGroup.trim().uppercase() == cleanBloodGroup
                                    }

                                    when {
                                        duplicatePhone -> {
                                            addDonorMessage =
                                                "A donor with this phone number already exists. Please check the donor list before adding again."
                                            showAddDonorDialog = true
                                        }

                                        duplicateNameAreaBloodGroup -> {
                                            addDonorMessage =
                                                "A similar donor already exists with the same name, area, and blood group."
                                            showAddDonorDialog = true
                                        }

                                        else -> {
                                            donorViewModel.addDonor(
                                                Donor(
                                                    name = name,
                                                    bloodGroup = bloodGroup,
                                                    phone = phone,
                                                    area = area,
                                                    availabilityStatus = if (available) "Available" else "Unavailable",
                                                    notes = notes,
                                                    updatedTimestamp = System.currentTimeMillis()
                                                )
                                            )

                                            addDonorMessage = ""
                                            showAddDonorDialog = false
                                            currentScreen = Routes.HOME
                                        }
                                    }
                                },
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.DONOR_LIST -> {
                            DonorListScreen(
                                donors = donors,
                                onEditDonorClick = { donor ->
                                    selectedDonor = donor
                                    currentScreen = Routes.EDIT_DONOR
                                },
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.EDIT_DONOR -> {
                            val donorToEdit = selectedDonor

                            if (donorToEdit == null) {
                                currentScreen = Routes.DONOR_LIST
                            } else {
                                EditDonorScreen(
                                    donor = donorToEdit,
                                    onSaveClick = { updatedDonor ->
                                        donorViewModel.updateDonor(updatedDonor)
                                        selectedDonor = null
                                        currentScreen = Routes.DONOR_LIST
                                    },
                                    onDeleteClick = { donorToDelete ->
                                        donorViewModel.deleteDonor(donorToDelete)
                                        selectedDonor = null
                                        currentScreen = Routes.DONOR_LIST
                                    },
                                    onBackClick = {
                                        selectedDonor = null
                                        currentScreen = Routes.DONOR_LIST
                                    }
                                )
                            }
                        }

                        Routes.EMERGENCY -> {
                            EmergencyScreen(
                                donors = donors,
                                onUpdateDonor = { updatedDonor ->
                                    donorViewModel.updateDonor(updatedDonor)
                                },
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.DASHBOARD -> {
                            DashboardScreen(
                                donors = donors,
                                onBackClick = {
                                    currentScreen = Routes.HOME
                                }
                            )
                        }

                        Routes.CSV_TOOLS -> {
                            CsvToolsScreen(
                                donors = donors,
                                onImportDonors = { importedDonors ->
                                    importedDonors.forEach { donor ->
                                        donorViewModel.addDonor(donor)
                                    }
                                },
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