
package com.ananyasacademics.bloodconnect.domain.validation

object DonorValidator {

    fun validateName(name: String): String? {
        return if (name.trim().length < 2) {
            "Please enter the donor's full name."
        } else null
    }

    fun validateBloodGroup(bloodGroup: String): String? {
        return if (bloodGroup == "Select Blood Group") {
            "Please select a blood group."
        } else null
    }

    fun validatePhone(phone: String): String? {
        val digitsOnly = phone.filter { it.isDigit() }
        return if (digitsOnly.length < 10) {
            "Please enter a valid phone number with at least 10 digits."
        } else null
    }

    fun validateArea(area: String): String? {
        return if (area.trim().isEmpty()) {
            "Please enter the donor's area or city."
        } else null
    }
}