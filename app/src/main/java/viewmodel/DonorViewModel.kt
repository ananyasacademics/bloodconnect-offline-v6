package com.ananyasacademics.bloodconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ananyasacademics.bloodconnect.data.model.Donor
import com.ananyasacademics.bloodconnect.data.repository.DonorRepository
import com.ananyasacademics.bloodconnect.data.repository.DonorSaveResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonorViewModel(
    private val repository: DonorRepository
) : ViewModel() {

    val allDonors: Flow<List<Donor>> = repository.allDonors

    private val _saveMessage = MutableStateFlow("")
    val saveMessage: StateFlow<String> = _saveMessage

    fun addDonor(
        donor: Donor
    ) {
        viewModelScope.launch {
            val result = repository.insertDonorWithDuplicateCheck(donor)

            _saveMessage.value = when (result) {
                DonorSaveResult.Success ->
                    "Donor saved successfully."

                DonorSaveResult.DuplicatePhone ->
                    "A donor with this phone number already exists. Please check the donor list before adding again."

                DonorSaveResult.DuplicateNameAreaBloodGroup ->
                    "A similar donor already exists with the same name, area, and blood group."

                DonorSaveResult.Error ->
                    "Unable to save donor. Please check the information and try again."
            }
        }
    }

    fun clearSaveMessage() {
        _saveMessage.value = ""
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