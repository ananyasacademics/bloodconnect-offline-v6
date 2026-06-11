package com.ananyasacademics.bloodconnect.data.repository

import com.ananyasacademics.bloodconnect.data.local.DonorDao
import com.ananyasacademics.bloodconnect.data.model.Donor
import kotlinx.coroutines.flow.Flow

sealed class DonorSaveResult {
    object Success : DonorSaveResult()
    object DuplicatePhone : DonorSaveResult()
    object DuplicateNameAreaBloodGroup : DonorSaveResult()
    object Error : DonorSaveResult()
}

class DonorRepository(
    private val donorDao: DonorDao
) {
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors()

    suspend fun insertDonor(donor: Donor) {
        donorDao.insertDonor(donor)
    }

    suspend fun insertDonorWithDuplicateCheck(donor: Donor): DonorSaveResult {
        return try {
            val existingDonors = donorDao.getAllDonorsOnce()

            val cleanPhone = donor.phone.filter { it.isDigit() }
            val cleanName = donor.name.trim().lowercase()
            val cleanArea = donor.area.trim().lowercase()
            val cleanBloodGroup = donor.bloodGroup.trim().uppercase()

            val duplicatePhone = existingDonors.any {
                it.phone.filter { char -> char.isDigit() } == cleanPhone
            }

            if (duplicatePhone) {
                return DonorSaveResult.DuplicatePhone
            }

            val duplicateNameAreaBloodGroup = existingDonors.any {
                it.name.trim().lowercase() == cleanName &&
                        it.area.trim().lowercase() == cleanArea &&
                        it.bloodGroup.trim().uppercase() == cleanBloodGroup
            }

            if (duplicateNameAreaBloodGroup) {
                return DonorSaveResult.DuplicateNameAreaBloodGroup
            }

            donorDao.insertDonor(
                donor.copy(updatedTimestamp = System.currentTimeMillis())
            )

            DonorSaveResult.Success
        } catch (e: Exception) {
            DonorSaveResult.Error
        }
    }

    suspend fun updateDonor(donor: Donor) {
        donorDao.updateDonor(
            donor.copy(updatedTimestamp = System.currentTimeMillis())
        )
    }

    suspend fun deleteDonor(donor: Donor) {
        donorDao.deleteDonor(donor)
    }
}