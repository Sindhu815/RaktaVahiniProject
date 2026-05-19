package com.healthcare.raktavahini.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthcare.raktavahini.data.model.Donor

@Dao
interface DonorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donor: Donor)

    @Update
    suspend fun update(donor: Donor)

    @Delete
    suspend fun delete(donor: Donor)

    @Query("SELECT * FROM donors ORDER BY name ASC")
    fun getAllDonors(): LiveData<List<Donor>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup ORDER BY name ASC")
    fun getDonorsByBloodGroup(bloodGroup: String): LiveData<List<Donor>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup AND isAvailable = 1 ORDER BY name ASC")
    fun getAvailableDonors(bloodGroup: String): LiveData<List<Donor>>

    @Query(
        """
        SELECT * FROM donors
        WHERE bloodGroup = :bloodGroup
        AND isAvailable = 1
        AND (:locationQuery = '' OR location LIKE '%' || :locationQuery || '%')
        ORDER BY name ASC
        """
    )
    fun emergencySearch(bloodGroup: String, locationQuery: String): LiveData<List<Donor>>
}
