package com.ananyasacademics.bloodconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.data.model.Donor
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    donors: List<Donor>,
    onBackClick: () -> Unit
) {
    val totalDonors = donors.size
    val availableDonors = donors.count {
        it.availabilityStatus.equals("Available", ignoreCase = true)
    }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    val bloodGroupCounts = bloodGroups.associateWith { group ->
        donors.count { it.bloodGroup == group }
    }

    val coveredBloodGroups = bloodGroupCounts.count { it.value > 0 }
    val missingBloodGroups = bloodGroups.filter { group ->
        (bloodGroupCounts[group] ?: 0) == 0
    }

    val now = System.currentTimeMillis()

    val freshDonors = donors.count { donor ->
        val ageDays = TimeUnit.MILLISECONDS.toDays(now - donor.updatedTimestamp)
        donor.updatedTimestamp > 0L && ageDays <= 30
    }

    val agingDonors = donors.count { donor ->
        val ageDays = TimeUnit.MILLISECONDS.toDays(now - donor.updatedTimestamp)
        donor.updatedTimestamp > 0L && ageDays in 31..90
    }

    val staleDonors = donors.count { donor ->
        val ageDays = TimeUnit.MILLISECONDS.toDays(now - donor.updatedTimestamp)
        donor.updatedTimestamp <= 0L || ageDays > 90
    }

    val notContactedCount = donors.count {
        it.outreachStatus.equals("Not Contacted", ignoreCase = true)
    }

    val callAttemptedCount = donors.count {
        it.outreachStatus.equals("Call Attempted", ignoreCase = true)
    }

    val smsSentCount = donors.count {
        it.outreachStatus.equals("SMS Sent", ignoreCase = true)
    }

    val reachedCount = donors.count {
        it.outreachStatus.equals("Reached", ignoreCase = true)
    }

    val unavailableOutreachCount = donors.count {
        it.outreachStatus.equals("Unavailable", ignoreCase = true)
    }

    val readinessMessage = when {
        totalDonors == 0 ->
            "Readiness: No donor records yet. Add donors to build local emergency preparedness."

        availableDonors == 0 ->
            "Readiness: Donor records exist, but none are currently marked available."

        coveredBloodGroups == 8 && staleDonors == 0 ->
            "Readiness: Strong coverage across all blood groups with no stale records."

        coveredBloodGroups >= 6 ->
            "Readiness: Strong local coverage across multiple blood groups."

        coveredBloodGroups >= 3 ->
            "Readiness: Moderate coverage. Continue adding donors across missing blood groups."

        else ->
            "Readiness: Early-stage coverage. Add more donors to improve emergency preparedness."
    }

    val coverageMessage = if (missingBloodGroups.isEmpty()) {
        "All 8 blood groups are represented."
    } else {
        "Missing: ${missingBloodGroups.joinToString(", ")}"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Preparedness intelligence for offline emergency donor coordination.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Core Readiness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Registered Donors")
                    Text(totalDonors.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available Donors")
                    Text(availableDonors.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Blood Groups Covered")
                    Text("$coveredBloodGroups / 8", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = readinessMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Data Freshness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fresh Records")
                    Text(freshDonors.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Aging Records")
                    Text(agingDonors.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Stale Records")
                    Text(staleDonors.toString(), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fresh = updated within 30 days. Stale = older than 90 days or unknown.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Coverage Gaps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = coverageMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Outreach Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Not Contacted")
                    Text(notContactedCount.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Call Attempted")
                    Text(callAttemptedCount.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("SMS Sent")
                    Text(smsSentCount.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reached")
                    Text(reachedCount.toString(), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Unavailable")
                    Text(unavailableOutreachCount.toString(), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Blood Group Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                bloodGroups.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = group,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${bloodGroupCounts[group] ?: 0}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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