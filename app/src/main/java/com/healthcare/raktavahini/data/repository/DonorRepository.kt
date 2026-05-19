package com.healthcare.raktavahini.data.repository

import androidx.lifecycle.LiveData
import com.healthcare.raktavahini.data.dao.DonationLogDao
import com.healthcare.raktavahini.data.dao.DonorDao
import com.healthcare.raktavahini.data.model.DonationLog
import com.healthcare.raktavahini.data.model.Donor

class DonorRepository(
    private val donorDao: DonorDao,
    private val donationLogDao: DonationLogDao
) {
    val allDonors: LiveData<List<Donor>> = donorDao.getAllDonors()
    val allDonationLogs: LiveData<List<DonationLog>> = donationLogDao.getAllLogs()

    suspend fun insert(donor: Donor) = donorDao.insert(donor)

    suspend fun update(donor: Donor) = donorDao.update(donor)

    suspend fun delete(donor: Donor) = donorDao.delete(donor)

    fun getDonorsByBloodGroup(bloodGroup: String): LiveData<List<Donor>> {
        return donorDao.getDonorsByBloodGroup(bloodGroup)
    }

    fun getAvailableDonors(bloodGroup: String): LiveData<List<Donor>> {
        return donorDao.getAvailableDonors(bloodGroup)
    }

    fun emergencySearch(bloodGroup: String, locationQuery: String): LiveData<List<Donor>> {
        return donorDao.emergencySearch(bloodGroup, locationQuery)
    }

    suspend fun insertDonationLog(log: DonationLog) = donationLogDao.insert(log)
}
