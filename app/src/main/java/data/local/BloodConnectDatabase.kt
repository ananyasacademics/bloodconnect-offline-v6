package com.ananyasacademics.bloodconnect.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ananyasacademics.bloodconnect.data.model.Donor

@Database(
    entities = [Donor::class],
    version = 2,
    exportSchema = false
)
abstract class BloodConnectDatabase : RoomDatabase() {

    abstract fun donorDao(): DonorDao

    companion object {
        @Volatile
        private var INSTANCE: BloodConnectDatabase? = null

        fun getDatabase(context: Context): BloodConnectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodConnectDatabase::class.java,
                    "bloodconnect_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}