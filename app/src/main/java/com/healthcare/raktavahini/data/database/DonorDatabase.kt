package com.healthcare.raktavahini.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.healthcare.raktavahini.data.dao.DonationLogDao
import com.healthcare.raktavahini.data.dao.DonorDao
import com.healthcare.raktavahini.data.model.DonationLog
import com.healthcare.raktavahini.data.model.Donor

@Database(entities = [Donor::class, DonationLog::class], version = 2, exportSchema = false)
abstract class DonorDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun donationLogDao(): DonationLogDao

    companion object {
        @Volatile
        private var INSTANCE: DonorDatabase? = null

        fun getDatabase(context: Context): DonorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonorDatabase::class.java,
                    "donor_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
