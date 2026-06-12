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
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.data.model.Donor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorListScreen(
    donors: List<Donor>,
    onEditDonorClick: (Donor) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var nameSearch by remember { mutableStateOf("") }
    var areaSearch by remember { mutableStateOf("") }
    var selectedBloodGroup by remember { mutableStateOf("All") }
    var selectedAvailability by remember { mutableStateOf("All") }

    var bloodGroupExpanded by remember { mutableStateOf(false) }
    var availabilityExpanded by remember { mutableStateOf(false) }

    val bloodGroupOptions = listOf("All", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    val availabilityOptions = listOf("All", "Available", "Unavailable")

    val filteredDonors = donors.filter { donor ->
        val matchesName = donor.name.contains(nameSearch.trim(), ignoreCase = true)
        val matchesArea = donor.area.contains(areaSearch.trim(), ignoreCase = true)
        val matchesBloodGroup = selectedBloodGroup == "All" || donor.bloodGroup == selectedBloodGroup
        val matchesAvailability =
            selectedAvailability == "All" ||
                    donor.availabilityStatus.equals(selectedAvailability, ignoreCase = true)

        matchesName && matchesArea && matchesBloodGroup && matchesAvailability
    }

    fun formatUpdatedTime(timestamp: Long): String {
        return try {
            val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            formatter.format(Date(timestamp))
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun reliabilityLabel(timestamp: Long): String {
        val ageMillis = System.currentTimeMillis() - timestamp
        val ageDays = TimeUnit.MILLISECONDS.toDays(ageMillis)

        return when {
            timestamp <= 0L -> "Reliability: Unknown update history"
            ageDays <= 30 -> "Reliability: Fresh record"
            ageDays <= 90 -> "Reliability: Aging record - verify if urgent"
            else -> "Reliability: Stale record - confirm before emergency use"
        }
    }

    fun availabilityLabel(status: String): String {
        return if (status.equals("Available", ignoreCase = true)) {
            "Availability: Available to contact"
        } else {
            "Availability: Not currently available"
        }
    }

    fun openDialer(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone.filter { it.isDigit() }}")
        }
        context.startActivity(intent)
    }

    fun openSms(phone: String, donorName: String) {
        val message = "Hello $donorName, this is an emergency blood donor coordination message from BloodConnect Offline. Please respond if you are available."
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${phone.filter { it.isDigit() }}")
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
            text = "Donor List",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Search, contact, and update local donor records.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameSearch,
            onValueChange = { nameSearch = it },
            label = { Text("Search by Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = areaSearch,
            onValueChange = { areaSearch = it },
            label = { Text("Search by Area") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = bloodGroupExpanded,
            onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }
        ) {
            OutlinedTextField(
                value = selectedBloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Blood Group Filter") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = bloodGroupExpanded,
                onDismissRequest = { bloodGroupExpanded = false }
            ) {
                bloodGroupOptions.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            selectedBloodGroup = group
                            bloodGroupExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = availabilityExpanded,
            onExpandedChange = { availabilityExpanded = !availabilityExpanded }
        ) {
            OutlinedTextField(
                value = selectedAvailability,
                onValueChange = {},
                readOnly = true,
                label = { Text("Availability Filter") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = availabilityExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = availabilityExpanded,
                onDismissRequest = { availabilityExpanded = false }
            ) {
                availabilityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedAvailability = option
                            availabilityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Showing ${filteredDonors.size} of ${donors.size} donors",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (donors.isEmpty()) {
            Text(
                text = "No donors saved yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else if (filteredDonors.isEmpty()) {
            Text(
                text = "No donors match the selected search or filter criteria.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredDonors) { donor ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = donor.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Blood Group: ${donor.bloodGroup}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = donor.availabilityStatus,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Text(
                                text = "Phone: ${donor.phone}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Area: ${donor.area}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = availabilityLabel(donor.availabilityStatus),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Last Updated: ${formatUpdatedTime(donor.updatedTimestamp)}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = reliabilityLabel(donor.updatedTimestamp),
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (donor.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Notes: ${donor.notes}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { openDialer(donor.phone) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Call")
                                }

                                OutlinedButton(
                                    onClick = { openSms(donor.phone, donor.name) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("SMS")
                                }

                                OutlinedButton(
                                    onClick = { onEditDonorClick(donor) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
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