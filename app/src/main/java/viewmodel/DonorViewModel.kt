package com.ananyasacademics.bloodconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ananyasacademics.bloodconnect.data.model.Donor
import com.ananyasacademics.bloodconnect.data.repository.DonorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DonorViewModel(
    private val repository: DonorRepository
) : ViewModel() {

    val allDonors: Flow<List<Donor>> = repository.allDonors

    fun addDonor(
        donor: Donor
    ) {
        viewModelScope.launch {
            repository.insertDonor(donor)
        }
    }

    fun updateDonor(
        donor: Donor
    ) {
        viewModelScope.launch {
            repository.updateDonor(donor)
        }
    }

    fun deleteDonor(
        donor: Donor
    ) {
        viewModelScope.launch {
            repository.deleteDonor(donor)
        }
    }
}