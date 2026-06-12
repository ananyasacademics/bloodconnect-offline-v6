package com.ananyasacademics.bloodconnect.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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

@Composable
fun CsvToolsScreen(
    donors: List<Donor>,
    onImportDonors: (List<Donor>) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var csvInput by remember { mutableStateOf("") }
    var pendingCsvText by remember { mutableStateOf("") }

    val validBloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    fun buildCsv(): String {
        val header = listOf(
            "Name",
            "Blood Group",
            "Phone",
            "Area",
            "Availability",
            "Notes",
            "Last Updated"
        ).joinToString(",")

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val rows = donors.map { donor ->
            listOf(
                escapeCsv(donor.name),
                escapeCsv(donor.bloodGroup),
                escapeCsv(donor.phone),
                escapeCsv(donor.area),
                escapeCsv(donor.availabilityStatus),
                escapeCsv(donor.notes),
                escapeCsv(formatter.format(Date(donor.updatedTimestamp)))
            ).joinToString(",")
        }

        return listOf(header, *rows.toTypedArray()).joinToString("\n")
    }

    fun buildFileName(): String {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        return "BloodConnect_Donors_${formatter.format(Date())}.csv"
    }

    fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            if (char == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (char == ',' && !inQuotes) {
                result.add(current.toString())
                current.clear()
            } else {
                current.append(char)
            }

            i++
        }

        result.add(current.toString())
        return result
    }

    fun processCsvImport(csvText: String) {
        val lines = csvText.lines().map { it.trim() }.filter { it.isNotBlank() }

        if (lines.size < 2) {
            message = "Please provide CSV data with a header row and at least one donor row."
            return
        }

        var imported = 0
        var skippedInvalid = 0
        var skippedDuplicate = 0

        val existingPhones = donors.map { it.phone.filter { c -> c.isDigit() } }.toMutableSet()
        val existingCombos = donors.map {
            "${it.name.trim().lowercase()}|${it.area.trim().lowercase()}|${it.bloodGroup.trim().uppercase()}"
        }.toMutableSet()

        val newDonors = mutableListOf<Donor>()

        lines.drop(1).forEach { line ->
            val columns = parseCsvLine(line)

            if (columns.size < 5) {
                skippedInvalid++
                return@forEach
            }

            val name = columns.getOrNull(0)?.trim().orEmpty()
            val bloodGroup = columns.getOrNull(1)?.trim().orEmpty()
            val phone = columns.getOrNull(2)?.trim().orEmpty()
            val area = columns.getOrNull(3)?.trim().orEmpty()
            val rawAvailability = columns.getOrNull(4)?.trim().orEmpty()
            val availability = rawAvailability.ifBlank { "Available" }
            val notes = columns.getOrNull(5)?.trim().orEmpty()

            val cleanPhone = phone.filter { it.isDigit() }
            val combo = "${name.lowercase()}|${area.lowercase()}|${bloodGroup.uppercase()}"

            val isValid = name.length in 2..60 &&
                    bloodGroup in validBloodGroups &&
                    cleanPhone.length in 7..15 &&
                    area.isNotBlank() &&
                    area.length <= 80 &&
                    notes.length <= 300 &&
                    (availability.equals("Available", true) || availability.equals("Unavailable", true))

            if (!isValid) {
                skippedInvalid++
                return@forEach
            }

            if (existingPhones.contains(cleanPhone) || existingCombos.contains(combo)) {
                skippedDuplicate++
                return@forEach
            }

            existingPhones.add(cleanPhone)
            existingCombos.add(combo)

            newDonors.add(
                Donor(
                    name = name,
                    bloodGroup = bloodGroup,
                    phone = phone,
                    area = area,
                    availabilityStatus = if (availability.equals("Available", true)) "Available" else "Unavailable",
                    notes = notes,
                    updatedTimestamp = System.currentTimeMillis()
                )
            )

            imported++
        }

        if (newDonors.isNotEmpty()) {
            onImportDonors(newDonors)
        }

        message = "CSV import complete.\n\nImported: $imported\nSkipped duplicates: $skippedDuplicate\nSkipped invalid rows: $skippedInvalid"
        csvInput = ""
    }

    val saveCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) {
            message = "CSV save was cancelled."
            return@rememberLauncherForActivityResult
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(pendingCsvText.toByteArray())
            }

            message = "CSV file saved successfully."
            pendingCsvText = ""
        } catch (e: Exception) {
            message = "Unable to save CSV file. Please try Share CSV instead."
        }
    }

    val importCsvFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            message = "CSV file import was cancelled."
            return@rememberLauncherForActivityResult
        }

        try {
            val csvText = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use {
                it.readText()
            }.orEmpty()

            if (csvText.isBlank()) {
                message = "The selected CSV file is empty or could not be read."
            } else {
                processCsvImport(csvText)
            }
        } catch (e: Exception) {
            message = "Unable to read selected CSV file. Please try paste import instead."
        }
    }

    fun saveCsvFile() {
        if (donors.isEmpty()) {
            message = "No donor records available to save."
            return
        }

        pendingCsvText = buildCsv()
        saveCsvLauncher.launch(buildFileName())
    }

    fun shareCsv() {
        if (donors.isEmpty()) {
            message = "No donor records available to export."
            return
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "BloodConnect Offline Donor Export")
            putExtra(Intent.EXTRA_TEXT, buildCsv())
        }

        context.startActivity(Intent.createChooser(sendIntent, "Share donor CSV"))
    }

    fun importCsvFromPaste() {
        processCsvImport(csvInput)
    }

    fun importCsvFromFile() {
        importCsvFileLauncher.launch(
            arrayOf(
                "text/csv",
                "text/comma-separated-values",
                "text/plain",
                "application/vnd.ms-excel",
                "*/*"
            )
        )
    }

    if (message.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { message = "" },
            title = { Text("CSV Tools") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { message = "" }) {
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
            text = "Data Tools",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Export and import local donor records for offline backup, portability, and continuity.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CSV Export",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Save a real CSV file on this device or share CSV text through available apps.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { saveCsvFile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save CSV File")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { shareCsv() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share CSV")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CSV File Import",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select a saved CSV file from this device. The app validates rows, skips duplicates, and imports valid donor records.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { importCsvFromFile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import CSV File")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Paste CSV Import",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Paste CSV text below as a backup method. The same validation and duplicate checks are applied.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = csvInput,
                    onValueChange = { csvInput = it },
                    label = { Text("Paste CSV here") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { importCsvFromPaste() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Pasted CSV")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Privacy Note",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CSV files may contain phone numbers and donor details. BloodConnect does not upload this data automatically.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}