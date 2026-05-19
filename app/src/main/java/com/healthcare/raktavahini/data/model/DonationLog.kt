package com.healthcare.raktavahini.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_logs")
data class DonationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorName: String,
    val bloodGroup: String,
    val location: String,
    val donatedAt: Long,
    val note: String = ""
)
