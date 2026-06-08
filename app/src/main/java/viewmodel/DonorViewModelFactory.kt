package com.ananyasacademics.bloodconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ananyasacademics.bloodconnect.data.repository.DonorRepository

class DonorViewModelFactory(
    private val repository: DonorRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(DonorViewModel::class.java)) {
            return DonorViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}