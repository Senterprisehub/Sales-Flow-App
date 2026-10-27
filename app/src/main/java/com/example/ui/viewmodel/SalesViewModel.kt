package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiInsightResult
import com.example.data.ai.GeminiAiService
import com.example.data.local.SalesDatabase
import com.example.data.model.SalesEntry
import com.example.data.repository.SalesRepository
import com.example.utils.CsvUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MonthlyMetric(
  val monthName: String,
  val monthShort: String,
  val monthIndex: Int,
  val target: Double,
  val achieved: Double,
  val entryCount: Int,
) {
  val percentage: Int
    get() = if (target > 0) Math.round((achieved / target) * 100).toInt() else if (achieved > 0) 100 else 0
}

data class QuarterMetric(
  val quarter: String,
  val target: Double,
  val achieved: Double,
  val entryCount: Int,
) {
  val percentage: Int
    get() = if (target > 0) Math.round((achieved / target) * 100).toInt() else if (achieved > 0) 100 else 0
}

data class PartyMetric(
  val partyName: String,
  val target: Double,
  val achieved: Double,
  val entryCount: Int,
) {
  val percentage: Int
    get() = if (target > 0) Math.round((achieved / target) * 100).toInt() else if (achieved > 0) 100 else 0
}

data class ProductMetric(
  val productName: String,
  val target: Double,
  val achieved: Double,
  val entryCount: Int,
) {
  val percentage: Int
    get() = if (target > 0) Math.round((achieved / target) * 100).toInt() else if (achieved > 0) 100 else 0
}

data class YearlySummary(
  val year: Int?,
  val totalTarget: Double,
  val totalAchieved: Double,
  val totalEntries: Int,
  val achievementPct: Int,
  val variance: Double, // achieved - target
  val avgDealSize: Double,
  val bestMonth: MonthlyMetric?,
  val bestParty: PartyMetric?,
  val topProduct: ProductMetric?,
  val monthlyMetrics: List<MonthlyMetric>,
  val quarterlyMetrics: List<QuarterMetric>,
  val topParties: List<PartyMetric>,
  val topProducts: List<ProductMetric>,
)

data class QuarterlyDetailReport(
  val selectedQuarter: String?, // "Q1", "Q2", "Q3", "Q4", or null for All
  val year: Int?,
  val partyFilter: String?,
  val productFilter: String?,
  val totalTarget: Double,
  val totalAchieved: Double,
  val totalEntries: Int,
  val achievementPct: Int,
  val variance: Double,
  val avgDealSize: Double,
  val allQuarterMetrics: List<QuarterMetric>,
  val quarterMonthlyMetrics: List<MonthlyMetric>,
  val topParties: List<PartyMetric>,
  val topProducts: List<ProductMetric>,
  val bestParty: PartyMetric?,
  val topProduct: ProductMetric?
)

class SalesViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: SalesRepository
  private val geminiAiService = GeminiAiService()

  // AI Insights State
  val aiInsightResult = MutableStateFlow<AiInsightResult?>(null)
  val isAiLoading = MutableStateFlow(false)
  val selectedAiFocus = MutableStateFlow("General Overview")

  init {
    val db = SalesDatabase.getDatabase(application)
    repository = SalesRepository(db.salesDao())
  }

  // Filter States
  val activePartyFilters = MutableStateFlow<Set<String>>(emptySet())
  val selectedMonth = MutableStateFlow<String?>(null)
  val selectedYear = MutableStateFlow<Int?>(Calendar.getInstance().get(Calendar.YEAR))
  val selectedProduct = MutableStateFlow<String?>(null)
  val searchQuery = MutableStateFlow("")

  // Yearly Analysis specific Party & Product Filters
  val yearlyPartyFilter = MutableStateFlow<String?>(null)
  val yearlyProductFilter = MutableStateFlow<String?>(null)

  // Quarterly Report specific Quarter, Party & Product Filters
  val selectedQuarter = MutableStateFlow<String?>(null) // null = All Quarters
  val quarterlyPartyFilter = MutableStateFlow<String?>(null)
  val quarterlyProductFilter = MutableStateFlow<String?>(null)

  // UI status messages
  val userMessage = MutableStateFlow<String?>(null)

  // Raw entries from DB
  val allEntries: StateFlow<List<SalesEntry>> = repository.allEntries.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Empty by default - no prefilled data is seeded automatically

  // Unique parties & products for autocompletion / management
  val knownParties: StateFlow<List<String>> = allEntries.combine(MutableStateFlow(Unit)) { entries, _ ->
    entries.map { it.party }.distinct().sorted()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val knownProducts: StateFlow<List<String>> = allEntries.combine(MutableStateFlow(Unit)) { entries, _ ->
    entries.map { it.product }.distinct().sorted()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val availableYears: StateFlow<List<Int>> = allEntries.combine(MutableStateFlow(Unit)) { entries, _ ->
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearsFromData = entries.map { it.year }.distinct()
    (yearsFromData + currentYear).distinct().sortedDescending()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(Calendar.getInstance().get(Calendar.YEAR)))

  // Filtered entries for main entries list
  val filteredEntries: StateFlow<List<SalesEntry>> = combine(
    allEntries,
    activePartyFilters,
    selectedMonth,
    selectedYear,
    combine(selectedProduct, searchQuery) { p, q -> Pair(p, q) }
  ) { entries, partyFilters, month, year, (product, query) ->
    entries.filter { entry ->
      val matchesParty = partyFilters.isEmpty() || partyFilters.any { p ->
        entry.party.contains(p, ignoreCase = true)
      }
      val matchesMonth = month.isNullOrEmpty() || entry.monthName.equals(month, ignoreCase = true)
      val matchesYear = year == null || entry.year == year
      val matchesProduct = product.isNullOrEmpty() || entry.product.contains(product, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
        entry.party.contains(query, ignoreCase = true) ||
        entry.product.contains(query, ignoreCase = true) ||
        entry.notes.contains(query, ignoreCase = true) ||
        entry.date.contains(query, ignoreCase = true)

      matchesParty && matchesMonth && matchesYear && matchesProduct && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Full Yearly Analysis Dashboard State based on selected year, party, and product filters
  val yearlySummary: StateFlow<YearlySummary> = combine(
    allEntries,
    selectedYear,
    yearlyPartyFilter,
    yearlyProductFilter
  ) { entries, year, partyFilter, productFilter ->
    val targetEntries = entries.filter { entry ->
      val matchesYear = year == null || entry.year == year
      val matchesParty = partyFilter.isNullOrEmpty() || entry.party.equals(partyFilter, ignoreCase = true)
      val matchesProduct = productFilter.isNullOrEmpty() || entry.product.equals(productFilter, ignoreCase = true)
      matchesYear && matchesParty && matchesProduct
    }

    val totalTarget = targetEntries.sumOf { it.targetValue }
    val totalAchieved = targetEntries.sumOf { it.achievementValue }
    val totalEntries = targetEntries.size
    val pct = if (totalTarget > 0) Math.round((totalAchieved / totalTarget) * 100).toInt() else if (totalAchieved > 0) 100 else 0
    val variance = totalAchieved - totalTarget
    val avgDeal = if (totalEntries > 0) totalAchieved / totalEntries else 0.0

    // Monthly breakdown (12 months)
    val months = listOf(
      "January" to "Jan", "February" to "Feb", "March" to "Mar", "April" to "Apr",
      "May" to "May", "June" to "Jun", "July" to "Jul", "August" to "Aug",
      "September" to "Sep", "October" to "Oct", "November" to "Nov", "December" to "Dec"
    )

    val monthlyMetrics = months.mapIndexed { index, (fullName, shortName) ->
      val monthNum = index + 1
      val inMonth = targetEntries.filter { it.monthNumber == monthNum }
      MonthlyMetric(
        monthName = fullName,
        monthShort = shortName,
        monthIndex = index,
        target = inMonth.sumOf { it.targetValue },
        achieved = inMonth.sumOf { it.achievementValue },
        entryCount = inMonth.size
      )
    }

    // Quarterly breakdown
    val quarters = listOf("Q1" to listOf(1, 2, 3), "Q2" to listOf(4, 5, 6), "Q3" to listOf(7, 8, 9), "Q4" to listOf(10, 11, 12))
    val quarterlyMetrics = quarters.map { (qName, monthNums) ->
      val inQ = targetEntries.filter { it.monthNumber in monthNums }
      QuarterMetric(
        quarter = qName,
        target = inQ.sumOf { it.targetValue },
        achieved = inQ.sumOf { it.achievementValue },
        entryCount = inQ.size
      )
    }

    // Top Parties
    val partyGroups = targetEntries.groupBy { it.party }
    val topParties = partyGroups.map { (name, list) ->
      PartyMetric(
        partyName = name,
        target = list.sumOf { it.targetValue },
        achieved = list.sumOf { it.achievementValue },
        entryCount = list.size
      )
    }.sortedByDescending { it.achieved }

    // Top Products
    val productGroups = targetEntries.groupBy { it.product }
    val topProducts = productGroups.map { (name, list) ->
      ProductMetric(
        productName = name,
        target = list.sumOf { it.targetValue },
        achieved = list.sumOf { it.achievementValue },
        entryCount = list.size
      )
    }.sortedByDescending { it.achieved }

    val bestMonth = monthlyMetrics.filter { it.achieved > 0 }.maxByOrNull { it.achieved }
    val bestParty = topParties.firstOrNull()
    val topProduct = topProducts.firstOrNull()

    YearlySummary(
      year = year,
      totalTarget = totalTarget,
      totalAchieved = totalAchieved,
      totalEntries = totalEntries,
      achievementPct = pct,
      variance = variance,
      avgDealSize = avgDeal,
      bestMonth = bestMonth,
      bestParty = bestParty,
      topProduct = topProduct,
      monthlyMetrics = monthlyMetrics,
      quarterlyMetrics = quarterlyMetrics,
      topParties = topParties,
      topProducts = topProducts,
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    YearlySummary(
      year = Calendar.getInstance().get(Calendar.YEAR),
      totalTarget = 0.0,
      totalAchieved = 0.0,
      totalEntries = 0,
      achievementPct = 0,
      variance = 0.0,
      avgDealSize = 0.0,
      bestMonth = null,
      bestParty = null,
      topProduct = null,
      monthlyMetrics = emptyList(),
      quarterlyMetrics = emptyList(),
      topParties = emptyList(),
      topProducts = emptyList()
    )
  )

  // Full Quarterly Analysis Report State based on selected quarter, year, party, and product filters
  val quarterlyDetailReport: StateFlow<QuarterlyDetailReport> = combine(
    allEntries,
    selectedYear,
    selectedQuarter,
    quarterlyPartyFilter,
    quarterlyProductFilter
  ) { entries, year, quarter, partyFilter, productFilter ->
    val quarterMonths = when (quarter) {
      "Q1" -> listOf(1, 2, 3)
      "Q2" -> listOf(4, 5, 6)
      "Q3" -> listOf(7, 8, 9)
      "Q4" -> listOf(10, 11, 12)
      else -> (1..12).toList()
    }

    // Base entries matching year, party, and product filters
    val baseFilteredEntries = entries.filter { entry ->
      val matchesYear = year == null || entry.year == year
      val matchesParty = partyFilter.isNullOrEmpty() || entry.party.equals(partyFilter, ignoreCase = true)
      val matchesProduct = productFilter.isNullOrEmpty() || entry.product.equals(productFilter, ignoreCase = true)
      matchesYear && matchesParty && matchesProduct
    }

    // Target entries for selected quarter
    val quarterEntries = baseFilteredEntries.filter { it.monthNumber in quarterMonths }

    val totalTarget = quarterEntries.sumOf { it.targetValue }
    val totalAchieved = quarterEntries.sumOf { it.achievementValue }
    val totalEntries = quarterEntries.size
    val pct = if (totalTarget > 0) Math.round((totalAchieved / totalTarget) * 100).toInt() else if (totalAchieved > 0) 100 else 0
    val variance = totalAchieved - totalTarget
    val avgDeal = if (totalEntries > 0) totalAchieved / totalEntries else 0.0

    // 4 Quarters overview based on the baseFilteredEntries (party & product filtered)
    val quarters = listOf(
      "Q1" to listOf(1, 2, 3),
      "Q2" to listOf(4, 5, 6),
      "Q3" to listOf(7, 8, 9),
      "Q4" to listOf(10, 11, 12)
    )
    val allQuarterMetrics = quarters.map { (qName, monthNums) ->
      val inQ = baseFilteredEntries.filter { it.monthNumber in monthNums }
      QuarterMetric(
        quarter = qName,
        target = inQ.sumOf { it.targetValue },
        achieved = inQ.sumOf { it.achievementValue },
        entryCount = inQ.size
      )
    }

    // Months for the selected quarter
    val monthsList = listOf(
      1 to ("January" to "Jan"),
      2 to ("February" to "Feb"),
      3 to ("March" to "Mar"),
      4 to ("April" to "Apr"),
      5 to ("May" to "May"),
      6 to ("June" to "Jun"),
      7 to ("July" to "Jul"),
      8 to ("August" to "Aug"),
      9 to ("September" to "Sep"),
      10 to ("October" to "Oct"),
      11 to ("November" to "Nov"),
      12 to ("December" to "Dec")
    )
    val activeMonths = monthsList.filter { it.first in quarterMonths }
    val quarterMonthlyMetrics = activeMonths.map { (monthNum, namePair) ->
      val (fullName, shortName) = namePair
      val inMonth = quarterEntries.filter { it.monthNumber == monthNum }
      MonthlyMetric(
        monthName = fullName,
        monthShort = shortName,
        monthIndex = monthNum - 1,
        target = inMonth.sumOf { it.targetValue },
        achieved = inMonth.sumOf { it.achievementValue },
        entryCount = inMonth.size
      )
    }

    // Top Parties for the selected quarter
    val partyGroups = quarterEntries.groupBy { it.party }
    val topParties = partyGroups.map { (name, list) ->
      PartyMetric(
        partyName = name,
        target = list.sumOf { it.targetValue },
        achieved = list.sumOf { it.achievementValue },
        entryCount = list.size
      )
    }.sortedByDescending { it.achieved }

    // Top Products for the selected quarter
    val productGroups = quarterEntries.groupBy { it.product }
    val topProducts = productGroups.map { (name, list) ->
      ProductMetric(
        productName = name,
        target = list.sumOf { it.targetValue },
        achieved = list.sumOf { it.achievementValue },
        entryCount = list.size
      )
    }.sortedByDescending { it.achieved }

    QuarterlyDetailReport(
      selectedQuarter = quarter,
      year = year,
      partyFilter = partyFilter,
      productFilter = productFilter,
      totalTarget = totalTarget,
      totalAchieved = totalAchieved,
      totalEntries = totalEntries,
      achievementPct = pct,
      variance = variance,
      avgDealSize = avgDeal,
      allQuarterMetrics = allQuarterMetrics,
      quarterMonthlyMetrics = quarterMonthlyMetrics,
      topParties = topParties,
      topProducts = topProducts,
      bestParty = topParties.firstOrNull(),
      topProduct = topProducts.firstOrNull()
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    QuarterlyDetailReport(
      selectedQuarter = null,
      year = Calendar.getInstance().get(Calendar.YEAR),
      partyFilter = null,
      productFilter = null,
      totalTarget = 0.0,
      totalAchieved = 0.0,
      totalEntries = 0,
      achievementPct = 0,
      variance = 0.0,
      avgDealSize = 0.0,
      allQuarterMetrics = emptyList(),
      quarterMonthlyMetrics = emptyList(),
      topParties = emptyList(),
      topProducts = emptyList(),
      bestParty = null,
      topProduct = null
    )
  )

  // Yearly Analysis Filter Actions
  fun setYearlyPartyFilter(party: String?) {
    yearlyPartyFilter.value = if (party?.isBlank() == true) null else party
  }

  fun setYearlyProductFilter(product: String?) {
    yearlyProductFilter.value = if (product?.isBlank() == true) null else product
  }

  fun clearYearlyFilters() {
    yearlyPartyFilter.value = null
    yearlyProductFilter.value = null
    selectedYear.value = Calendar.getInstance().get(Calendar.YEAR)
  }

  // Quarterly Analysis Filter Actions
  fun setSelectedQuarter(quarter: String?) {
    selectedQuarter.value = quarter
  }

  fun setQuarterlyPartyFilter(party: String?) {
    quarterlyPartyFilter.value = if (party?.isBlank() == true) null else party
  }

  fun setQuarterlyProductFilter(product: String?) {
    quarterlyProductFilter.value = if (product?.isBlank() == true) null else product
  }

  fun clearQuarterlyFilters() {
    selectedQuarter.value = null
    quarterlyPartyFilter.value = null
    quarterlyProductFilter.value = null
    selectedYear.value = Calendar.getInstance().get(Calendar.YEAR)
  }

  // Filter actions
  fun addPartyFilter(party: String) {
    val trimmed = party.trim()
    if (trimmed.isNotEmpty()) {
      activePartyFilters.value = activePartyFilters.value + trimmed
    }
  }

  fun removePartyFilter(party: String) {
    activePartyFilters.value = activePartyFilters.value - party
  }

  fun setMonthFilter(month: String?) {
    selectedMonth.value = if (month?.isBlank() == true) null else month
  }

  fun setYearFilter(year: Int?) {
    selectedYear.value = year
  }

  fun setProductFilter(product: String?) {
    selectedProduct.value = if (product?.isBlank() == true) null else product
  }

  fun setSearchQuery(query: String) {
    searchQuery.value = query
  }

  fun clearFilters() {
    activePartyFilters.value = emptySet()
    selectedMonth.value = null
    selectedProduct.value = null
    searchQuery.value = ""
    userMessage.value = "Filters cleared"
  }

  // CRUD Operations
  fun addEntry(
    party: String,
    date: String,
    product: String,
    targetValue: Double,
    achievementValue: Double,
    notes: String
  ) {
    viewModelScope.launch {
      val entry = SalesEntry(
        id = UUID.randomUUID().toString(),
        party = party.trim(),
        date = date.trim(),
        product = product.trim(),
        targetValue = targetValue,
        achievementValue = achievementValue,
        notes = notes.trim(),
        createdAt = System.currentTimeMillis()
      )
      repository.insert(entry)
      userMessage.value = "Entry added for ${entry.party}"
    }
  }

  fun updateEntry(entry: SalesEntry) {
    viewModelScope.launch {
      repository.update(entry)
      userMessage.value = "Updated entry for ${entry.party}"
    }
  }

  fun deleteEntry(id: String) {
    viewModelScope.launch {
      repository.deleteById(id)
      userMessage.value = "Entry deleted"
    }
  }

  fun deleteParties(parties: List<String>) {
    viewModelScope.launch {
      repository.deleteByParties(parties)
      activePartyFilters.value = activePartyFilters.value - parties.toSet()
      userMessage.value = "Deleted records for ${parties.size} parties"
    }
  }

  fun deleteProducts(products: List<String>) {
    viewModelScope.launch {
      repository.deleteByProducts(products)
      if (selectedProduct.value in products) {
        selectedProduct.value = null
      }
      userMessage.value = "Deleted records for ${products.size} products"
    }
  }

  fun resetAllData() {
    viewModelScope.launch {
      repository.deleteAll()
      clearFilters()
      userMessage.value = "All data has been reset"
    }
  }

  fun importCsv(context: Context, uri: Uri) {
    viewModelScope.launch {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
          val importedEntries = CsvUtils.parseCsv(inputStream)
          if (importedEntries.isNotEmpty()) {
            repository.insertAll(importedEntries)
            userMessage.value = "Successfully imported ${importedEntries.size} entries"
          } else {
            userMessage.value = "No valid entries found in CSV"
          }
        } else {
          userMessage.value = "Failed to read CSV file"
        }
      } catch (e: Exception) {
        userMessage.value = "Error importing CSV: ${e.localizedMessage}"
      }
    }
  }

  fun exportCsvToCache(context: Context): File? {
    return try {
      val dataToExport = filteredEntries.value.ifEmpty { allEntries.value }
      val csvContent = CsvUtils.generateCsv(dataToExport)
      val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
      val file = File(context.cacheDir, "sales_flow_$dateStr.csv")
      FileOutputStream(file).use { out ->
        out.write(csvContent.toByteArray())
      }
      file
    } catch (e: Exception) {
      null
    }
  }

  fun clearUserMessage() {
    userMessage.value = null
  }

  fun refreshAiInsights(focus: String = selectedAiFocus.value) {
    selectedAiFocus.value = focus
    viewModelScope.launch {
      isAiLoading.value = true
      try {
        val entries = allEntries.value
        val summary = yearlySummary.value
        val result = geminiAiService.generateOverallInsights(
          allEntries = entries,
          topProducts = summary.topProducts,
          topParties = summary.topParties,
          totalRevenue = summary.totalAchieved,
          totalTarget = summary.totalTarget,
          analysisFocus = focus
        )
        aiInsightResult.value = result
      } catch (e: Exception) {
        userMessage.value = "Failed to generate AI insights: ${e.localizedMessage}"
      } finally {
        isAiLoading.value = false
      }
    }
  }

  private suspend fun seedSampleData() {
    val curYear = Calendar.getInstance().get(Calendar.YEAR)
    val prevYear = curYear - 1
    val sampleList = listOf(
      SalesEntry(UUID.randomUUID().toString(), "Acme Corp", "$curYear-01-15", "Enterprise Software", 450000.0, 480000.0, "Q1 Renewal & Add-on"),
      SalesEntry(UUID.randomUUID().toString(), "Global Tech", "$curYear-01-28", "Cloud Hosting", 250000.0, 230000.0, "Monthly Managed Services"),
      SalesEntry(UUID.randomUUID().toString(), "Metro Solutions", "$curYear-02-10", "Hardware Server", 600000.0, 650000.0, "Data center upgrade"),
      SalesEntry(UUID.randomUUID().toString(), "Zenith Retail", "$curYear-02-22", "POS Terminal", 180000.0, 195000.0, "5 new store locations"),
      SalesEntry(UUID.randomUUID().toString(), "Acme Corp", "$curYear-03-05", "Security Suite", 300000.0, 290000.0, "Annual cybersecurity pack"),
      SalesEntry(UUID.randomUUID().toString(), "Apex Logistics", "$curYear-03-18", "Fleet Tracker", 520000.0, 560000.0, "GPS IoT units"),
      SalesEntry(UUID.randomUUID().toString(), "Global Tech", "$curYear-04-12", "Enterprise Software", 400000.0, 420000.0, "User tier upgrade"),
      SalesEntry(UUID.randomUUID().toString(), "Nexus Infra", "$curYear-04-25", "Cloud Hosting", 350000.0, 310000.0, "Storage cluster"),
      SalesEntry(UUID.randomUUID().toString(), "Zenith Retail", "$curYear-05-14", "POS Terminal", 220000.0, 250000.0, "Summer sale deployment"),
      SalesEntry(UUID.randomUUID().toString(), "Metro Solutions", "$curYear-06-08", "Security Suite", 280000.0, 300000.0, "Firewall expansion"),
      SalesEntry(UUID.randomUUID().toString(), "Apex Logistics", "$curYear-07-20", "Fleet Tracker", 480000.0, 470000.0, "Mid-year fleet add"),
      SalesEntry(UUID.randomUUID().toString(), "Acme Corp", "$curYear-08-11", "Cloud Hosting", 320000.0, 360000.0, "Multi-region scale"),
      // Previous year for comparison
      SalesEntry(UUID.randomUUID().toString(), "Acme Corp", "$prevYear-02-14", "Enterprise Software", 380000.0, 400000.0, "Initial pilot"),
      SalesEntry(UUID.randomUUID().toString(), "Global Tech", "$prevYear-05-20", "Cloud Hosting", 200000.0, 180000.0, "Year-1 hosting"),
      SalesEntry(UUID.randomUUID().toString(), "Metro Solutions", "$prevYear-09-12", "Hardware Server", 500000.0, 510000.0, "Rack servers")
    )
    repository.insertAll(sampleList)
  }
}
