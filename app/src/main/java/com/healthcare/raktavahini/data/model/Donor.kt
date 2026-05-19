package com.healthcare.raktavahini.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.healthcare.raktavahini.utils.Constants

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val bloodGroup: String,
    val phoneNumber: String,
    val location: String,
    val lastDonationDate: Long,
    val isAvailable: Boolean = true,
    val age: Int,
    val email: String = ""
) {
    fun isEligible(): Boolean {
        val millisPerDay = 1000L * 60L * 60L * 24L
        val daysSinceDonation = (System.currentTimeMillis() - lastDonationDate) / millisPerDay
        return isAvailable && daysSinceDonation >= Constants.ELIGIBILITY_DAYS
    }
}
