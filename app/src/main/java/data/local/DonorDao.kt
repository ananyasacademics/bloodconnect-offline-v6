package com.ananyasacademics.bloodconnect.data.local

import androidx.room.*
import com.ananyasacademics.bloodconnect.data.model.Donor
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {

    @Query("SELECT * FROM donors ORDER BY name ASC")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors")
    suspend fun getAllDonorsOnce(): List<Donor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Delete
    suspend fun deleteDonor(donor: Donor)
}