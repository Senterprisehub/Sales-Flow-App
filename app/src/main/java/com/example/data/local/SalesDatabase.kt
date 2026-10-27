package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SalesEntryEntity::class], version = 1, exportSchema = false)
abstract class SalesDatabase : RoomDatabase() {
  abstract fun salesDao(): SalesDao

  companion object {
    @Volatile
    private var INSTANCE: SalesDatabase? = null

    fun getDatabase(context: Context): SalesDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SalesDatabase::class.java,
          "sales_flow_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
