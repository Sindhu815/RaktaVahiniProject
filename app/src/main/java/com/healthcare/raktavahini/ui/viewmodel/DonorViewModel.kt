package com.healthcare.raktavahini.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.healthcare.raktavahini.data.database.DonorDatabase
import com.healthcare.raktavahini.data.model.DonationLog
import com.healthcare.raktavahini.data.model.Donor
import com.healthcare.raktavahini.data.repository.DonorRepository
import kotlinx.coroutines.launch

class DonorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DonorRepository
    val allDonors: LiveData<List<Donor>>
    val allDonationLogs: LiveData<List<DonationLog>>

    init {
        val database = DonorDatabase.getDatabase(application)
        repository = DonorRepository(database.donorDao(), database.donationLogDao())
        allDonors = repository.allDonors
        allDonationLogs = repository.allDonationLogs
    }

    fun insert(donor: Donor) = viewModelScope.launch {
        repository.insert(donor)
    }

    fun searchByBloodGroup(bloodGroup: String): LiveData<List<Donor>> {
        return repository.getDonorsByBloodGroup(bloodGroup)
    }

    fun getAvailableDonors(bloodGroup: String): LiveData<List<Donor>> {
        return repository.getAvailableDonors(bloodGroup)
    }

    fun emergencySearch(bloodGroup: String, locationQuery: String): LiveData<List<Donor>> {
        return repository.emergencySearch(bloodGroup, locationQuery)
    }

    fun insertDonationLog(log: DonationLog) = viewModelScope.launch {
        repository.insertDonationLog(log)
    }
}
