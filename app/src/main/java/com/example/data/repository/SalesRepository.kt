package com.example.data.repository

import com.example.data.local.SalesDao
import com.example.data.local.SalesEntryEntity
import com.example.data.model.SalesEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SalesRepository(private val salesDao: SalesDao) {

  val allEntries: Flow<List<SalesEntry>> = salesDao.getAllEntries().map { entities ->
    entities.map { it.toDomain() }
  }

  suspend fun insert(entry: SalesEntry) {
    salesDao.insert(SalesEntryEntity.fromDomain(entry))
  }

  suspend fun insertAll(entries: List<SalesEntry>) {
    salesDao.insertAll(entries.map { SalesEntryEntity.fromDomain(it) })
  }

  suspend fun update(entry: SalesEntry) {
    salesDao.update(SalesEntryEntity.fromDomain(entry))
  }

  suspend fun deleteById(id: String) {
    salesDao.deleteById(id)
  }

  suspend fun deleteByParties(parties: List<String>) {
    salesDao.deleteByParties(parties)
  }

  suspend fun deleteByProducts(products: List<String>) {
    salesDao.deleteByProducts(products)
  }

  suspend fun deleteAll() {
    salesDao.deleteAll()
  }
}
