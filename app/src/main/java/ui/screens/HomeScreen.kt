package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.R

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
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onEmergencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.emergency_mode))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddDonorClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_donor))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDonorListClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.donor_list))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDashboardClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.dashboard))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCsvToolsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.data_tools))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPrivacyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.privacy_info))
            }
        }

        Text(
            text = stringResource(R.string.local_only),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}