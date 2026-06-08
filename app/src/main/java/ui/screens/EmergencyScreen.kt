package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.data.model.Donor
import com.ananyasacademics.bloodconnect.domain.compatibility.BloodCompatibilityEngine

@Composable
fun EmergencyScreen(
    donors: List<Donor>,
    onBackClick: () -> Unit
) {
    var requestedBloodGroup by remember { mutableStateOf("O+") }
    var expanded by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    val compatibleGroups = BloodCompatibilityEngine.compatibleDonorGroups(requestedBloodGroup)

    val matchingDonors = donors
        .filter { donor ->
            donor.availabilityStatus == "Available" &&
                    compatibleGroups.contains(donor.bloodGroup)
        }
        .sortedWith(
            compareBy<Donor> {
                if (it.bloodGroup == requestedBloodGroup) 0 else 1
            }.thenBy {
                it.name
            }
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Emergency Blood Request",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find compatible local donors from this device, even without internet.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Needed Blood Group",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(requestedBloodGroup)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            bloodGroups.forEach { group ->
                DropdownMenuItem(
                    text = {
                        Text(group)
                    },
                    onClick = {
                        requestedBloodGroup = group
                        expanded = false
                        hasSearched = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                hasSearched = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Matching Donors")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (hasSearched) {
            Text(
                text = "Matches for $requestedBloodGroup",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (matchingDonors.isEmpty()) {
                Text(
                    text = "No available compatible donors found on this device.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(matchingDonors) { donor ->
                        val label = BloodCompatibilityEngine.matchLabel(
                            requestedBloodGroup = requestedBloodGroup,
                            donorBloodGroup = donor.bloodGroup
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = donor.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = "Blood Group: ${donor.bloodGroup}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Phone: ${donor.phone}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Area: ${donor.area}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (donor.notes.isNotBlank()) {
                                    Text(
                                        text = "Notes: ${donor.notes}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Select a needed blood group, then tap Find Matching Donors.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}