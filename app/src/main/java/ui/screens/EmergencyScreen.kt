package com.ananyasacademics.bloodconnect.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.data.model.Donor
import com.ananyasacademics.bloodconnect.domain.compatibility.BloodCompatibilityEngine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun EmergencyScreen(
    donors: List<Donor>,
    onUpdateDonor: (Donor) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var requestedBloodGroup by remember { mutableStateOf("O+") }
    var expanded by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    val compatibleGroups = BloodCompatibilityEngine.compatibleDonorGroups(requestedBloodGroup)

    val matchingDonors = donors
        .filter { donor ->
            donor.availabilityStatus.equals("Available", ignoreCase = true) &&
                    compatibleGroups.contains(donor.bloodGroup)
        }
        .sortedWith(
            compareBy<Donor> {
                if (it.bloodGroup == requestedBloodGroup) 0 else 1
            }.thenBy {
                if (it.outreachStatus == "Reached") 1 else 0
            }.thenBy {
                it.name
            }
        )

    val exactMatches = matchingDonors.count { it.bloodGroup == requestedBloodGroup }
    val compatibleMatches = matchingDonors.count { it.bloodGroup != requestedBloodGroup }
    val availableDonors = donors.count {
        it.availabilityStatus.equals("Available", ignoreCase = true)
    }

    fun reliabilityLabel(timestamp: Long): String {
        val ageMillis = System.currentTimeMillis() - timestamp
        val ageDays = TimeUnit.MILLISECONDS.toDays(ageMillis)

        return when {
            timestamp <= 0L -> "Update status unknown"
            ageDays <= 30 -> "Fresh record"
            ageDays <= 90 -> "Aging record - verify if urgent"
            else -> "Stale record - confirm before emergency use"
        }
    }

    fun formatContactTime(timestamp: Long): String {
        if (timestamp <= 0L) return "No outreach recorded"

        return try {
            val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            formatter.format(Date(timestamp))
        } catch (e: Exception) {
            "Unknown time"
        }
    }

    fun updateOutreachStatus(donor: Donor, status: String) {
        onUpdateDonor(
            donor.copy(
                outreachStatus = status,
                lastContactTimestamp = System.currentTimeMillis(),
                updatedTimestamp = System.currentTimeMillis()
            )
        )
    }

    fun openDialer(donor: Donor) {
        updateOutreachStatus(donor, "Call Attempted")

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${donor.phone.filter { it.isDigit() }}")
        }
        context.startActivity(intent)
    }

    fun openSms(donor: Donor) {
        updateOutreachStatus(donor, "SMS Sent")

        val message =
            "Hello ${donor.name}, this is an emergency blood donor coordination message from BloodConnect Offline. A $requestedBloodGroup request is being coordinated. Please respond if you are available."

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${donor.phone.filter { it.isDigit() }}")
            putExtra("sms_body", message)
        }

        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Emergency Mode",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Offline-compatible donor matching and outreach tracking for urgent coordination.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Emergency Blood Request",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Needed Blood Group",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = requestedBloodGroup,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    bloodGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group) },
                            onClick = {
                                requestedBloodGroup = group
                                expanded = false
                                hasSearched = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { hasSearched = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Find Matching Donors")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Local Readiness Snapshot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Total records: ${donors.size}")
                Text("Available donors: $availableDonors")

                if (hasSearched) {
                    Text("Exact matches: $exactMatches")
                    Text("Compatible backups: $compatibleMatches")
                } else {
                    Text("Select a blood group to calculate exact and compatible matches.")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hasSearched) {
            Text(
                text = "Matches for $requestedBloodGroup",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (matchingDonors.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = "No available compatible donors found on this device.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Try checking unavailable donors, importing a backup CSV, or updating the local donor list.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = donor.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("Blood Group: ${donor.bloodGroup}")
                                Text("Phone: ${donor.phone}")
                                Text("Area: ${donor.area}")

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Reliability: ${reliabilityLabel(donor.updatedTimestamp)}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "Outreach: ${donor.outreachStatus}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Last Contact: ${formatContactTime(donor.lastContactTimestamp)}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (donor.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Notes: ${donor.notes}")
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { openDialer(donor) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Call")
                                    }

                                    OutlinedButton(
                                        onClick = { openSms(donor) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("SMS")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { updateOutreachStatus(donor, "Reached") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reached")
                                    }

                                    OutlinedButton(
                                        onClick = { updateOutreachStatus(donor, "Unavailable") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Unavailable")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { updateOutreachStatus(donor, "Not Contacted") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Reset Outreach Status")
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