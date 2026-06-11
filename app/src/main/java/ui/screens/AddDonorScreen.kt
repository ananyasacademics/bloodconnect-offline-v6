package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDonorScreen(
    message: String = "",
    showMessageDialog: Boolean = false,
    onDismissMessage: () -> Unit = {},
    onSaveClick: (
        name: String,
        bloodGroup: String,
        phone: String,
        area: String,
        available: Boolean,
        notes: String
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var phone by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var available by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var bloodGroupExpanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    fun isPhoneValid(value: String): Boolean {
        val cleaned = value.filter { it.isDigit() }
        return cleaned.length in 7..15
    }

    if (showMessageDialog && message.isNotBlank()) {
        AlertDialog(
            onDismissRequest = onDismissMessage,
            title = {
                Text("Donor Not Saved")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                TextButton(
                    onClick = onDismissMessage
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Add Donor",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add local donor details for offline emergency coordination. Data stays on this device.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = ""
            },
            label = { Text("Donor Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage.contains("name", ignoreCase = true)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = bloodGroupExpanded,
            onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }
        ) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Blood Group") },
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
                bloodGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            bloodGroup = group
                            bloodGroupExpanded = false
                            errorMessage = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                errorMessage = ""
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage.contains("phone", ignoreCase = true)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = area,
            onValueChange = {
                area = it
                errorMessage = ""
            },
            label = { Text("Area / Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage.contains("area", ignoreCase = true)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Available to contact",
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = available,
            onCheckedChange = { available = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
                errorMessage = ""
            },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val cleanName = name.trim()
                val cleanPhone = phone.trim()
                val cleanArea = area.trim()
                val cleanNotes = notes.trim()

                errorMessage = when {
                    cleanName.isBlank() ->
                        "Please enter the donor name."

                    cleanName.length < 2 ->
                        "Please enter a valid donor name."

                    cleanName.length > 60 ->
                        "Donor name is too long. Please keep it under 60 characters."

                    bloodGroup !in bloodGroups ->
                        "Please select a valid blood group."

                    cleanPhone.isBlank() ->
                        "Please enter a phone number."

                    !isPhoneValid(cleanPhone) ->
                        "Please enter a valid phone number with 7 to 15 digits."

                    cleanArea.isBlank() ->
                        "Please enter an area or location."

                    cleanArea.length > 80 ->
                        "Area is too long. Please keep it under 80 characters."

                    cleanNotes.length > 300 ->
                        "Notes are too long. Please keep notes under 300 characters."

                    else -> ""
                }

                if (errorMessage.isBlank()) {
                    onSaveClick(
                        cleanName,
                        bloodGroup,
                        cleanPhone,
                        cleanArea,
                        available,
                        cleanNotes
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Donor")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}