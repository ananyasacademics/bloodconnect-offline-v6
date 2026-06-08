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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ananyasacademics.bloodconnect.data.model.Donor

@Composable
fun DonorListScreen(
    donors: List<Donor>,
    onBackClick: () -> Unit
) {
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
            text = "Saved local donor records.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (donors.isEmpty()) {
            Text(
                text = "No donors saved yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(donors) { donor ->
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

                            Text(
                                text = "Status: ${donor.availabilityStatus}",
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

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}