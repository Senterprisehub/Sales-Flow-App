package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {
  @Query("SELECT * FROM sales_entries ORDER BY date DESC, createdAt DESC")
  fun getAllEntries(): Flow<List<SalesEntryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entry: SalesEntryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entries: List<SalesEntryEntity>)

  @Update
  suspend fun update(entry: SalesEntryEntity)

  @Query("DELETE FROM sales_entries WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM sales_entries WHERE party IN (:parties)")
  suspend fun deleteByParties(parties: List<String>)

  @Query("DELETE FROM sales_entries WHERE product IN (:products)")
  suspend fun deleteByProducts(products: List<String>)

  @Query("DELETE FROM sales_entries")
  suspend fun deleteAll()
}
