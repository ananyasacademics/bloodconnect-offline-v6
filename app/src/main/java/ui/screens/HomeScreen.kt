package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bloodconnect_logo),
                contentDescription = "BloodConnect Offline logo",
                modifier = Modifier.size(56.dp)
            )

            Column(
                modifier = Modifier.padding(start = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Offline emergency continuity",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Offline-first humanitarian emergency coordination infrastructure for resilient donor access during connectivity failures.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Emergency Ready",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Find compatible local donors, coordinate outreach, and track emergency contact status without relying on cloud access.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onEmergencyClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.emergency_mode))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Coordination Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onAddDonorClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_donor))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDonorListClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.donor_list))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDashboardClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.dashboard))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Preparedness & Trust",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Backup data, import CSV files, review privacy, and maintain local emergency readiness.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onCsvToolsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.data_tools))
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onPrivacyClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.privacy_info))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.local_only),
            style = MaterialTheme.typography.bodySmall
        )
    }
}