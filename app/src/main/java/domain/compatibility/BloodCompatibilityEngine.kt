
package com.ananyasacademics.bloodconnect.domain.compatibility

object BloodCompatibilityEngine {

    fun compatibleDonorGroups(requestedBloodGroup: String): List<String> {
        return when (requestedBloodGroup) {
            "O-" -> listOf("O-")
            "O+" -> listOf("O+", "O-")
            "A-" -> listOf("A-", "O-")
            "A+" -> listOf("A+", "A-", "O+", "O-")
            "B-" -> listOf("B-", "O-")
            "B+" -> listOf("B+", "B-", "O+", "O-")
            "AB-" -> listOf("AB-", "A-", "B-", "O-")
            "AB+" -> listOf("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-")
            else -> emptyList()
        }
    }

    fun matchLabel(requestedBloodGroup: String, donorBloodGroup: String): String {
        return if (requestedBloodGroup == donorBloodGroup) {
            "Exact Match"
        } else {
            "Compatible"
        }
    }
}