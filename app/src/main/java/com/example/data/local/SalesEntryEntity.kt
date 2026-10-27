package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SalesEntry

@Entity(tableName = "sales_entries")
data class SalesEntryEntity(
  @PrimaryKey val id: String,
  val party: String,
  val date: String,
  val product: String,
  val targetValue: Double,
  val achievementValue: Double,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
) {
  fun toDomain(): SalesEntry = SalesEntry(
    id = id,
    party = party,
    date = date,
    product = product,
    targetValue = targetValue,
    achievementValue = achievementValue,
    notes = notes,
    createdAt = createdAt,
  )

  companion object {
    fun fromDomain(entry: SalesEntry): SalesEntryEntity = SalesEntryEntity(
      id = entry.id,
      party = entry.party,
      date = entry.date,
      product = entry.product,
      targetValue = entry.targetValue,
      achievementValue = entry.achievementValue,
      notes = entry.notes,
      createdAt = entry.createdAt,
    )
  }
}
