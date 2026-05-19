package com.healthcare.raktavahini.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.healthcare.raktavahini.data.model.DonationLog

@Dao
interface DonationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: DonationLog)

    @Query("SELECT * FROM donation_logs ORDER BY donatedAt DESC")
    fun getAllLogs(): LiveData<List<DonationLog>>
}
