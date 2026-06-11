package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddDonorClick: () -> Unit,
    onDonorListClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onCsvToolsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "BloodConnect Offline",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Offline-first emergency donor coordination.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEmergencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Emergency Mode")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddDonorClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Donor")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDonorListClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Donor List")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDashboardClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dashboard")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCsvToolsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Data Tools")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPrivacyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Privacy & Info")
            }
        }

        Text(
            text = "Local-only storage • No cloud required",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}