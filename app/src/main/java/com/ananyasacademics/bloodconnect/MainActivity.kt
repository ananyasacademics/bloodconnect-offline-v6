package com.ananyasacademics.bloodconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.ananyasacademics.bloodconnect.ui.screens.HomeScreen
import com.ananyasacademics.bloodconnect.ui.theme.BloodConnectOfflineV6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloodConnectOfflineV6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    HomeScreen(
                        onAddDonorClick = {},
                        onDonorListClick = {},
                        onEmergencyClick = {},
                        onDashboardClick = {},
                        onPrivacyClick = {}
                    )
                }
            }
        }
    }
}