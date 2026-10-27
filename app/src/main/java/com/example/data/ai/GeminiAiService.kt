package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.SalesEntry
import com.example.ui.viewmodel.PartyMetric
import com.example.ui.viewmodel.ProductMetric
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Universal AI Service integrating Open-Source On-Device AI Engine (100% Offline)
 * with zero required cloud dependencies.
 */
class GeminiAiService {

  private val openSourceEngine = OpenSourceAiEngine()

  /**
   * Generates AI insights. Works completely offline using on-device open-source algorithms.
   */
  suspend fun generateOverallInsights(
    allEntries: List<SalesEntry>,
    topProducts: List<ProductMetric>,
    topParties: List<PartyMetric>,
    totalRevenue: Double,
    totalTarget: Double,
    analysisFocus: String = "General Overview"
  ): AiInsightResult = withContext(Dispatchers.Default) {
    // Primary execution on 100% On-Device Open-Source AI Engine (Offline First)
    return@withContext openSourceEngine.generateInsights(
      allEntries = allEntries,
      topProducts = topProducts,
      topParties = topParties,
      totalRevenue = totalRevenue,
      totalTarget = totalTarget,
      analysisFocus = analysisFocus
    )
  }
}
