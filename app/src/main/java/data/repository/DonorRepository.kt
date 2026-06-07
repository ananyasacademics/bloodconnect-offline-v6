
package com.ananyasacademics.bloodconnect.data.repository

import com.ananyasacademics.bloodconnect.data.local.DonorDao
import com.ananyasacademics.bloodconnect.data.model.Donor
import kotlinx.coroutines.flow.Flow

class DonorRepository(
    private val donorDao: DonorDao
) {
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors()

    suspend fun insertDonor(donor: Donor) {
        donorDao.insertDonor(donor)
    }

    suspend fun updateDonor(donor: Donor) {
        donorDao.updateDonor(donor)
    }

    suspend fun deleteDonor(donor: Donor) {
        donorDao.deleteDonor(donor)
    }
}