package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.ai.AiInsightResult
import com.example.data.model.SalesEntry
import com.example.ui.viewmodel.PartyMetric
import com.example.ui.viewmodel.ProductMetric
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

  /**
   * Generates a complete, comprehensive, multi-page Enterprise Sales & Performance PDF Report.
   * Extracts and renders all data dimensions without truncation:
   * 1. Executive Master Summary & KPI Overview
   * 2. Open-Source AI Intelligence & Strategic Analysis
   * 3. Yearly Performance Breakdown (All Recorded Years)
   * 4. Quarterly Performance Breakdown (All Quarters / Year-Quarters)
   * 5. Monthly Performance Breakdown (All 12 Months / Year-Months)
   * 6. Complete Party / Client Account Breakdown (All Parties)
   * 7. Complete Product Portfolio Breakdown (All Products)
   * 8. Granular Itemized Sales Transaction Records (All Entries)
   */
  fun generateExpandedReportPdf(
    context: Context,
    allEntries: List<SalesEntry>,
    topProducts: List<ProductMetric>,
    topParties: List<PartyMetric>,
    totalRevenue: Double,
    totalTarget: Double,
    aiInsightResult: AiInsightResult?,
    selectedFocus: String
  ): File {
    val pdfDocument = PdfDocument()
    val pageWidth = 595 // Standard A4 width at 72dpi
    val pageHeight = 842 // Standard A4 height at 72dpi
    val margin = 32f
    val contentWidth = pageWidth - (margin * 2)

    var pageNumber = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var currentPage = pdfDocument.startPage(pageInfo)
    var canvas: Canvas = currentPage.canvas

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var currentY = margin

    fun checkAndCreateNewPage(neededHeight: Float, sectionTitleOnContinuation: String? = null) {
      if (currentY + neededHeight > pageHeight - 36f) {
        // Draw bottom footer on current page
        paint.color = Color.rgb(156, 163, 175)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(
          "Sales Tracker Enterprise Report • Generated ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date())}",
          margin,
          pageHeight - 16f,
          paint
        )
        val pageStr = "Page $pageNumber"
        val pageStrWidth = paint.measureText(pageStr)
        canvas.drawText(pageStr, pageWidth - margin - pageStrWidth, pageHeight - 16f, paint)

        pdfDocument.finishPage(currentPage)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        currentPage = pdfDocument.startPage(pageInfo)
        canvas = currentPage.canvas
        currentY = margin

        // Draw continuation header if requested
        if (sectionTitleOnContinuation != null) {
          paint.color = Color.rgb(243, 244, 246)
          canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 18f, 3f, 3f, paint)
          paint.color = Color.rgb(75, 85, 99)
          paint.textSize = 8.5f
          paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
          canvas.drawText("$sectionTitleOnContinuation (Continued - Page $pageNumber)", margin + 6f, currentY + 12f, paint)
          currentY += 24f
        }
      }
    }

    // =========================================================================
    // SECTION 1: REPORT HEADER & EXECUTIVE OVERVIEW
    // =========================================================================
    paint.color = Color.rgb(30, 58, 138) // Deep Corporate Navy Blue
    canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 54f, 6f, 6f, paint)

    paint.color = Color.WHITE
    paint.textSize = 16f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("SALES TRACKER ENTERPRISE REPORT", margin + 14f, currentY + 24f, paint)

    paint.textSize = 9.5f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("Complete Multi-Dimensional Audit • Yearly, Quarterly, Monthly, Party & Product Analytics", margin + 14f, currentY + 42f, paint)

    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date())
    paint.textSize = 8.5f
    val dateWidth = paint.measureText("Exported: $dateStr")
    canvas.drawText("Exported: $dateStr", pageWidth - margin - dateWidth - 12f, currentY + 42f, paint)

    currentY += 64f

    // Calculate aggregated metrics
    val totalEntriesCount = allEntries.size
    val calcTarget = if (totalTarget > 0) totalTarget else allEntries.sumOf { it.targetValue }
    val calcAchieved = if (totalRevenue > 0) totalRevenue else allEntries.sumOf { it.achievementValue }
    val variance = calcAchieved - calcTarget
    val totalPct = CurrencyFormatter.formatPercentage(calcTarget, calcAchieved)
    val avgDeal = if (totalEntriesCount > 0) calcAchieved / totalEntriesCount else 0.0
    val uniquePartiesCount = allEntries.map { it.party }.distinct().size
    val uniqueProductsCount = allEntries.map { it.product }.distinct().size

    // Top 4 Metric KPI Cards (2x2 grid)
    val cardW = (contentWidth - 12f) / 2f
    val cardH = 38f

    // KPI 1: Target
    paint.color = Color.rgb(243, 244, 246)
    canvas.drawRoundRect(margin, currentY, margin + cardW, currentY + cardH, 5f, 5f, paint)
    paint.color = Color.rgb(107, 114, 128)
    paint.textSize = 8f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("TOTAL PLANNED TARGET", margin + 8f, currentY + 13f, paint)
    paint.color = Color.rgb(17, 24, 39)
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(CurrencyFormatter.formatRupees(calcTarget), margin + 8f, currentY + 30f, paint)

    // KPI 2: Achieved
    val col2X = margin + cardW + 12f
    paint.color = Color.rgb(236, 253, 245)
    canvas.drawRoundRect(col2X, currentY, col2X + cardW, currentY + cardH, 5f, 5f, paint)
    paint.color = Color.rgb(5, 150, 105)
    paint.textSize = 8f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("TOTAL REVENUE ACHIEVED", col2X + 8f, currentY + 13f, paint)
    paint.color = Color.rgb(6, 95, 70)
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(CurrencyFormatter.formatRupees(calcAchieved), col2X + 8f, currentY + 30f, paint)

    currentY += cardH + 6f

    // KPI 3: Attainment & Variance
    paint.color = if (variance >= 0) Color.rgb(239, 246, 255) else Color.rgb(254, 242, 242)
    canvas.drawRoundRect(margin, currentY, margin + cardW, currentY + cardH, 5f, 5f, paint)
    paint.color = Color.rgb(107, 114, 128)
    paint.textSize = 8f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("COMPLETION / NET VARIANCE", margin + 8f, currentY + 13f, paint)
    paint.color = if (variance >= 0) Color.rgb(29, 78, 216) else Color.rgb(185, 28, 28)
    paint.textSize = 12f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    val varSign = if (variance >= 0) "+${CurrencyFormatter.formatRupees(variance)}" else "-${CurrencyFormatter.formatRupees(Math.abs(variance))}"
    canvas.drawText("$totalPct ($varSign)", margin + 8f, currentY + 30f, paint)

    // KPI 4: Operations Overview
    paint.color = Color.rgb(249, 250, 251)
    canvas.drawRoundRect(col2X, currentY, col2X + cardW, currentY + cardH, 5f, 5f, paint)
    paint.color = Color.rgb(107, 114, 128)
    paint.textSize = 8f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText("OPERATIONS & PORTFOLIO", col2X + 8f, currentY + 13f, paint)
    paint.color = Color.rgb(31, 41, 55)
    paint.textSize = 11f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("$totalEntriesCount Deals • $uniquePartiesCount Clients • $uniqueProductsCount Products", col2X + 8f, currentY + 30f, paint)

    currentY += cardH + 16f

    // =========================================================================
    // SECTION 2: OPEN-SOURCE AI INTELLIGENCE & COMMERCIAL ASSESSMENT
    // =========================================================================
    if (aiInsightResult != null) {
      checkAndCreateNewPage(140f)

      // Section Header Banner
      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(37, 99, 235)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("1. OPEN-SOURCE AI STRATEGIC ANALYSIS ($selectedFocus)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // AI Summary Box
      paint.color = Color.rgb(249, 250, 251)
      val summaryLines = wrapText(aiInsightResult.summary, contentWidth - 20f, 9f, false)
      val summaryBoxHeight = (summaryLines.size * 13f) + 14f
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + summaryBoxHeight, 5f, 5f, paint)

      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 9f
      paint.typeface = Typeface.DEFAULT
      var lineY = currentY + 12f
      for (line in summaryLines) {
        canvas.drawText(line, margin + 8f, lineY, paint)
        lineY += 13f
      }
      currentY += summaryBoxHeight + 10f

      // Key Highlights
      if (aiInsightResult.keyHighlights.isNotEmpty()) {
        checkAndCreateNewPage(30f + (aiInsightResult.keyHighlights.size * 14f))
        paint.color = Color.rgb(5, 150, 105)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Key Strategic Highlights:", margin, currentY, paint)
        currentY += 12f

        paint.color = Color.rgb(55, 65, 81)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        for (h in aiInsightResult.keyHighlights) {
          val lines = wrapText("• $h", contentWidth - 10f, 8.5f, false)
          for (l in lines) {
            checkAndCreateNewPage(14f)
            canvas.drawText(l, margin + 4f, currentY, paint)
            currentY += 12f
          }
        }
        currentY += 6f
      }

      // Exposure & Risks
      if (aiInsightResult.opportunitiesAndRisks.isNotEmpty()) {
        checkAndCreateNewPage(30f + (aiInsightResult.opportunitiesAndRisks.size * 14f))
        paint.color = Color.rgb(217, 119, 6)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Opportunities & Risk Exposure:", margin, currentY, paint)
        currentY += 12f

        paint.color = Color.rgb(55, 65, 81)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        for (r in aiInsightResult.opportunitiesAndRisks) {
          val lines = wrapText("• $r", contentWidth - 10f, 8.5f, false)
          for (l in lines) {
            checkAndCreateNewPage(14f)
            canvas.drawText(l, margin + 4f, currentY, paint)
            currentY += 12f
          }
        }
        currentY += 6f
      }

      // Recommendations
      if (aiInsightResult.strategicRecommendations.isNotEmpty()) {
        checkAndCreateNewPage(30f + (aiInsightResult.strategicRecommendations.size * 14f))
        paint.color = Color.rgb(79, 70, 229)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Actionable Recommendations:", margin, currentY, paint)
        currentY += 12f

        paint.color = Color.rgb(55, 65, 81)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        for (rec in aiInsightResult.strategicRecommendations) {
          val lines = wrapText("• $rec", contentWidth - 10f, 8.5f, false)
          for (l in lines) {
            checkAndCreateNewPage(14f)
            canvas.drawText(l, margin + 4f, currentY, paint)
            currentY += 12f
          }
        }
        currentY += 10f
      }
    }

    // =========================================================================
    // SECTION 3: YEARLY PERFORMANCE BREAKDOWN (ALL YEARS)
    // =========================================================================
    val yearGroups = allEntries.groupBy { it.year }.toSortedMap(compareByDescending { it })
    if (yearGroups.isNotEmpty()) {
      checkAndCreateNewPage(60f + (yearGroups.size * 18f))

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("2. YEARLY PERFORMANCE BREAKDOWN (${yearGroups.size} Years Recorded)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 8f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("YEAR", margin + 6f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 120f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 230f, currentY + 11f, paint)
      canvas.drawText("VARIANCE", margin + 340f, currentY + 11f, paint)
      canvas.drawText("RATE %", margin + 440f, currentY + 11f, paint)
      canvas.drawText("DEALS", margin + 490f, currentY + 11f, paint)

      currentY += 18f

      var yIdx = 0
      for ((year, entries) in yearGroups) {
        checkAndCreateNewPage(18f, "Yearly Performance")
        val yTarget = entries.sumOf { it.targetValue }
        val yAchieved = entries.sumOf { it.achievementValue }
        val yVar = yAchieved - yTarget
        val yPctStr = CurrencyFormatter.formatPercentage(yTarget, yAchieved)
        val yPctVal = if (yTarget > 0) (yAchieved / yTarget) * 100.0 else 0.0

        paint.color = if (yIdx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 14f, paint)

        paint.color = Color.rgb(31, 41, 55)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("FY $year", margin + 6f, currentY + 10f, paint)

        paint.typeface = Typeface.DEFAULT
        canvas.drawText(CurrencyFormatter.formatRupees(yTarget), margin + 120f, currentY + 10f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(yAchieved), margin + 230f, currentY + 10f, paint)

        paint.color = if (yVar >= 0) Color.rgb(29, 78, 216) else Color.rgb(185, 28, 28)
        paint.typeface = Typeface.DEFAULT
        val varStr = if (yVar >= 0) "+${CurrencyFormatter.formatRupees(yVar)}" else "-${CurrencyFormatter.formatRupees(Math.abs(yVar))}"
        canvas.drawText(varStr, margin + 340f, currentY + 10f, paint)

        paint.color = if (yPctVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(yPctStr, margin + 440f, currentY + 10f, paint)

        paint.color = Color.rgb(107, 114, 128)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${entries.size}", margin + 490f, currentY + 10f, paint)

        currentY += 16f
        yIdx++
      }

      currentY += 12f
    }

    // =========================================================================
    // SECTION 4: QUARTERLY PERFORMANCE BREAKDOWN (ALL QUARTERS)
    // =========================================================================
    // Group by Year and Quarter
    val quarterGroups = allEntries.groupBy { entry ->
      val q = (entry.monthNumber - 1) / 3 + 1
      "FY ${entry.year} Q$q"
    }.toSortedMap()

    if (quarterGroups.isNotEmpty()) {
      checkAndCreateNewPage(60f + (quarterGroups.size * 18f))

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("3. QUARTERLY PERFORMANCE BREAKDOWN (${quarterGroups.size} Quarters)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 8f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("QUARTER", margin + 6f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 120f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 230f, currentY + 11f, paint)
      canvas.drawText("VARIANCE", margin + 340f, currentY + 11f, paint)
      canvas.drawText("RATE %", margin + 440f, currentY + 11f, paint)
      canvas.drawText("DEALS", margin + 490f, currentY + 11f, paint)

      currentY += 18f

      var qIdx = 0
      for ((qName, entries) in quarterGroups) {
        checkAndCreateNewPage(18f, "Quarterly Performance")
        val qTarget = entries.sumOf { it.targetValue }
        val qAchieved = entries.sumOf { it.achievementValue }
        val qVar = qAchieved - qTarget
        val qPctStr = CurrencyFormatter.formatPercentage(qTarget, qAchieved)
        val qPctVal = if (qTarget > 0) (qAchieved / qTarget) * 100.0 else 0.0

        paint.color = if (qIdx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 14f, paint)

        paint.color = Color.rgb(31, 41, 55)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(qName, margin + 6f, currentY + 10f, paint)

        paint.typeface = Typeface.DEFAULT
        canvas.drawText(CurrencyFormatter.formatRupees(qTarget), margin + 120f, currentY + 10f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(qAchieved), margin + 230f, currentY + 10f, paint)

        paint.color = if (qVar >= 0) Color.rgb(29, 78, 216) else Color.rgb(185, 28, 28)
        paint.typeface = Typeface.DEFAULT
        val varStr = if (qVar >= 0) "+${CurrencyFormatter.formatRupees(qVar)}" else "-${CurrencyFormatter.formatRupees(Math.abs(qVar))}"
        canvas.drawText(varStr, margin + 340f, currentY + 10f, paint)

        paint.color = if (qPctVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(qPctStr, margin + 440f, currentY + 10f, paint)

        paint.color = Color.rgb(107, 114, 128)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${entries.size}", margin + 490f, currentY + 10f, paint)

        currentY += 16f
        qIdx++
      }

      currentY += 12f
    }

    // =========================================================================
    // SECTION 5: MONTHLY PERFORMANCE BREAKDOWN (ALL RECORDED MONTHS)
    // =========================================================================
    val monthGroups = allEntries.groupBy { entry ->
      "${entry.year}-${String.format(Locale.US, "%02d", entry.monthNumber)}: ${entry.monthName} ${entry.year}"
    }.toSortedMap()

    if (monthGroups.isNotEmpty()) {
      checkAndCreateNewPage(60f + (monthGroups.size * 18f))

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("4. MONTHLY PERFORMANCE BREAKDOWN (${monthGroups.size} Months)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 8f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("MONTH", margin + 6f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 120f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 230f, currentY + 11f, paint)
      canvas.drawText("VARIANCE", margin + 340f, currentY + 11f, paint)
      canvas.drawText("RATE %", margin + 440f, currentY + 11f, paint)
      canvas.drawText("DEALS", margin + 490f, currentY + 11f, paint)

      currentY += 18f

      var mIdx = 0
      for ((key, entries) in monthGroups) {
        checkAndCreateNewPage(18f, "Monthly Performance")
        val displayMonth = key.substringAfter(": ")
        val mTarget = entries.sumOf { it.targetValue }
        val mAchieved = entries.sumOf { it.achievementValue }
        val mVar = mAchieved - mTarget
        val mPctStr = CurrencyFormatter.formatPercentage(mTarget, mAchieved)
        val mPctVal = if (mTarget > 0) (mAchieved / mTarget) * 100.0 else 0.0

        paint.color = if (mIdx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 14f, paint)

        paint.color = Color.rgb(31, 41, 55)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(displayMonth, margin + 6f, currentY + 10f, paint)

        canvas.drawText(CurrencyFormatter.formatRupees(mTarget), margin + 120f, currentY + 10f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(mAchieved), margin + 230f, currentY + 10f, paint)

        paint.color = if (mVar >= 0) Color.rgb(29, 78, 216) else Color.rgb(185, 28, 28)
        paint.typeface = Typeface.DEFAULT
        val varStr = if (mVar >= 0) "+${CurrencyFormatter.formatRupees(mVar)}" else "-${CurrencyFormatter.formatRupees(Math.abs(mVar))}"
        canvas.drawText(varStr, margin + 340f, currentY + 10f, paint)

        paint.color = if (mPctVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(mPctStr, margin + 440f, currentY + 10f, paint)

        paint.color = Color.rgb(107, 114, 128)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${entries.size}", margin + 490f, currentY + 10f, paint)

        currentY += 16f
        mIdx++
      }

      currentY += 12f
    }

    // =========================================================================
    // SECTION 6: COMPLETE PARTY / CLIENT BREAKDOWN (ALL PARTIES)
    // =========================================================================
    val allPartyGroups = allEntries.groupBy { it.party }
      .map { (name, entries) ->
        val pTarget = entries.sumOf { it.targetValue }
        val pAchieved = entries.sumOf { it.achievementValue }
        val pVar = pAchieved - pTarget
        val pPctStr = CurrencyFormatter.formatPercentage(pTarget, pAchieved)
        val pPctVal = if (pTarget > 0) (pAchieved / pTarget) * 100.0 else 0.0
        val share = if (calcAchieved > 0) (pAchieved / calcAchieved) * 100 else 0.0
        object {
          val party = name
          val target = pTarget
          val achieved = pAchieved
          val variance = pVar
          val percentageStr = pPctStr
          val percentageVal = pPctVal
          val count = entries.size
          val revenueShare = share
        }
      }.sortedByDescending { it.achieved }

    if (allPartyGroups.isNotEmpty()) {
      checkAndCreateNewPage(60f + (allPartyGroups.take(5).size * 18f))

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("5. PARTY-WISE CLIENT BREAKDOWN (${allPartyGroups.size} Total Clients)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 8f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("CLIENT / PARTY NAME", margin + 6f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 175f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 265f, currentY + 11f, paint)
      canvas.drawText("RATE %", margin + 365f, currentY + 11f, paint)
      canvas.drawText("SHARE %", margin + 430f, currentY + 11f, paint)
      canvas.drawText("DEALS", margin + 490f, currentY + 11f, paint)

      currentY += 18f

      allPartyGroups.forEachIndexed { idx, p ->
        checkAndCreateNewPage(18f, "Party-Wise Breakdown")
        paint.color = if (idx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 14f, paint)

        paint.color = Color.rgb(31, 41, 55)
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        val pName = if (p.party.length > 26) p.party.take(24) + ".." else p.party
        canvas.drawText("${idx + 1}. $pName", margin + 6f, currentY + 10f, paint)

        canvas.drawText(CurrencyFormatter.formatRupees(p.target), margin + 175f, currentY + 10f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(p.achieved), margin + 265f, currentY + 10f, paint)

        paint.color = if (p.percentageVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        canvas.drawText(p.percentageStr, margin + 365f, currentY + 10f, paint)

        paint.color = Color.rgb(79, 70, 229)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(String.format(Locale.US, "%.1f%%", p.revenueShare), margin + 430f, currentY + 10f, paint)

        paint.color = Color.rgb(107, 114, 128)
        canvas.drawText("${p.count}", margin + 490f, currentY + 10f, paint)

        currentY += 16f
      }

      currentY += 12f
    }

    // =========================================================================
    // SECTION 7: COMPLETE PRODUCT BREAKDOWN (ALL PRODUCTS)
    // =========================================================================
    val allProductGroups = allEntries.groupBy { it.product }
      .map { (name, entries) ->
        val prTarget = entries.sumOf { it.targetValue }
        val prAchieved = entries.sumOf { it.achievementValue }
        val prVar = prAchieved - prTarget
        val prPctStr = CurrencyFormatter.formatPercentage(prTarget, prAchieved)
        val prPctVal = if (prTarget > 0) (prAchieved / prTarget) * 100.0 else 0.0
        val share = if (calcAchieved > 0) (prAchieved / calcAchieved) * 100 else 0.0
        object {
          val product = name
          val target = prTarget
          val achieved = prAchieved
          val variance = prVar
          val percentageStr = prPctStr
          val percentageVal = prPctVal
          val count = entries.size
          val portfolioShare = share
        }
      }.sortedByDescending { it.achieved }

    if (allProductGroups.isNotEmpty()) {
      checkAndCreateNewPage(60f + (allProductGroups.take(5).size * 18f))

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("6. PRODUCT-WISE PORTFOLIO BREAKDOWN (${allProductGroups.size} Total Products)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 8f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("PRODUCT CATEGORY", margin + 6f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 175f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 265f, currentY + 11f, paint)
      canvas.drawText("RATE %", margin + 365f, currentY + 11f, paint)
      canvas.drawText("SHARE %", margin + 430f, currentY + 11f, paint)
      canvas.drawText("DEALS", margin + 490f, currentY + 11f, paint)

      currentY += 18f

      allProductGroups.forEachIndexed { idx, pr ->
        checkAndCreateNewPage(18f, "Product-Wise Breakdown")
        paint.color = if (idx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 14f, paint)

        paint.color = Color.rgb(31, 41, 55)
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        val prodName = if (pr.product.length > 26) pr.product.take(24) + ".." else pr.product
        canvas.drawText("${idx + 1}. $prodName", margin + 6f, currentY + 10f, paint)

        canvas.drawText(CurrencyFormatter.formatRupees(pr.target), margin + 175f, currentY + 10f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(pr.achieved), margin + 265f, currentY + 10f, paint)

        paint.color = if (pr.percentageVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        canvas.drawText(pr.percentageStr, margin + 365f, currentY + 10f, paint)

        paint.color = Color.rgb(79, 70, 229)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(String.format(Locale.US, "%.1f%%", pr.portfolioShare), margin + 430f, currentY + 10f, paint)

        paint.color = Color.rgb(107, 114, 128)
        canvas.drawText("${pr.count}", margin + 490f, currentY + 10f, paint)

        currentY += 16f
      }

      currentY += 12f
    }

    // =========================================================================
    // SECTION 8: COMPLETE GRANULAR SALES TRANSACTION LOG (ALL ENTRIES)
    // =========================================================================
    if (allEntries.isNotEmpty()) {
      checkAndCreateNewPage(60f)

      paint.color = Color.rgb(243, 244, 246)
      canvas.drawRoundRect(margin, currentY, pageWidth - margin, currentY + 20f, 4f, 4f, paint)
      paint.color = Color.rgb(31, 41, 55)
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("7. GRANULAR TRANSACTION RECORDS (${allEntries.size} Total Entries)", margin + 8f, currentY + 14f, paint)

      currentY += 26f

      // Table Header
      paint.color = Color.rgb(229, 231, 235)
      canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 16f, paint)
      paint.color = Color.rgb(75, 85, 99)
      paint.textSize = 7.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("#", margin + 3f, currentY + 11f, paint)
      canvas.drawText("DATE", margin + 20f, currentY + 11f, paint)
      canvas.drawText("PARTY / CLIENT", margin + 82f, currentY + 11f, paint)
      canvas.drawText("PRODUCT", margin + 210f, currentY + 11f, paint)
      canvas.drawText("TARGET", margin + 335f, currentY + 11f, paint)
      canvas.drawText("ACHIEVED", margin + 420f, currentY + 11f, paint)
      canvas.drawText("RATE", margin + 500f, currentY + 11f, paint)

      currentY += 18f

      val sortedEntries = allEntries.sortedWith(compareByDescending<SalesEntry> { it.date }.thenByDescending { it.id })

      sortedEntries.forEachIndexed { idx, entry ->
        checkAndCreateNewPage(16f, "Granular Transaction Records")
        paint.color = if (idx % 2 == 0) Color.WHITE else Color.rgb(249, 250, 251)
        canvas.drawRect(margin, currentY - 2f, pageWidth - margin, currentY + 13f, paint)

        paint.color = Color.rgb(107, 114, 128)
        paint.textSize = 7f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${idx + 1}", margin + 3f, currentY + 9f, paint)

        paint.color = Color.rgb(55, 65, 81)
        canvas.drawText(entry.date, margin + 20f, currentY + 9f, paint)

        val partyTrimmed = if (entry.party.length > 20) entry.party.take(18) + ".." else entry.party
        canvas.drawText(partyTrimmed, margin + 82f, currentY + 9f, paint)

        val prodTrimmed = if (entry.product.length > 18) entry.product.take(16) + ".." else entry.product
        canvas.drawText(prodTrimmed, margin + 210f, currentY + 9f, paint)

        canvas.drawText(CurrencyFormatter.formatRupees(entry.targetValue), margin + 335f, currentY + 9f, paint)

        paint.color = Color.rgb(5, 150, 105)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.formatRupees(entry.achievementValue), margin + 420f, currentY + 9f, paint)

        val rateStr = CurrencyFormatter.formatPercentage(entry.targetValue, entry.achievementValue)
        val rateVal = if (entry.targetValue > 0) (entry.achievementValue / entry.targetValue) * 100.0 else 0.0
        paint.color = if (rateVal >= 100) Color.rgb(5, 150, 105) else Color.rgb(217, 119, 6)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(rateStr, margin + 500f, currentY + 9f, paint)

        currentY += 14f
      }
    }

    // Final Page Footer
    paint.color = Color.rgb(156, 163, 175)
    paint.textSize = 8.5f
    paint.typeface = Typeface.DEFAULT
    canvas.drawText(
      "Sales Tracker Enterprise Report • Generated ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date())}",
      margin,
      pageHeight - 16f,
      paint
    )
    val finalPageStr = "Page $pageNumber"
    val finalPageStrWidth = paint.measureText(finalPageStr)
    canvas.drawText(finalPageStr, pageWidth - margin - finalPageStrWidth, pageHeight - 16f, paint)

    pdfDocument.finishPage(currentPage)

    // Save to Cache file
    val fileName = "Sales_Tracker_Comprehensive_Report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf"
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()

    return file
  }

  private fun wrapText(text: String, maxWidth: Float, textSize: Float, isBold: Boolean): List<String> {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.textSize = textSize
      this.typeface = if (isBold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
    }

    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()

    for (word in words) {
      val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
      val width = paint.measureText(testLine)
      if (width <= maxWidth) {
        currentLine = StringBuilder(testLine)
      } else {
        if (currentLine.isNotEmpty()) {
          lines.add(currentLine.toString())
        }
        currentLine = StringBuilder(word)
      }
    }
    if (currentLine.isNotEmpty()) {
      lines.add(currentLine.toString())
    }
    return lines
  }
}
