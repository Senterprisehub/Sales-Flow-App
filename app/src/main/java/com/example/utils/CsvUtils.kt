package com.example.utils

import com.example.data.model.SalesEntry
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

object CsvUtils {

  fun parseCsv(inputStream: InputStream): List<SalesEntry> {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val entries = mutableListOf<SalesEntry>()
    val lines = reader.readLines()

    if (lines.isEmpty()) return entries

    // Header index determination
    var headerRowIndex = 0
    var partyCol = 0
    var dateCol = 1
    var productCol = 2
    var targetCol = 3
    var achievementCol = 4
    var notesCol = 5

    // Check if first row is header
    val firstRow = parseCsvLine(lines[0])
    if (firstRow.any { it.equals("Party", ignoreCase = true) || it.equals("Product", ignoreCase = true) || it.equals("Target", ignoreCase = true) }) {
      firstRow.forEachIndexed { index, title ->
        val t = title.trim().lowercase()
        when {
          t.contains("party") -> partyCol = index
          t.contains("date") -> dateCol = index
          t.contains("product") -> productCol = index
          t.contains("target") -> targetCol = index
          t.contains("achiev") -> achievementCol = index
          t.contains("note") -> notesCol = index
        }
      }
      headerRowIndex = 1
    }

    for (i in headerRowIndex until lines.size) {
      val rawLine = lines[i].trim()
      if (rawLine.isBlank()) continue

      val cols = parseCsvLine(rawLine)
      if (cols.size < 3) continue

      val party = cols.getOrNull(partyCol)?.trim() ?: ""
      val date = cols.getOrNull(dateCol)?.trim() ?: ""
      val product = cols.getOrNull(productCol)?.trim() ?: ""
      val targetStr = cols.getOrNull(targetCol)?.trim()?.replace("₹", "")?.replace(",", "") ?: "0"
      val achieveStr = cols.getOrNull(achievementCol)?.trim()?.replace("₹", "")?.replace(",", "") ?: "0"
      val notes = cols.getOrNull(notesCol)?.trim() ?: ""

      val target = targetStr.toDoubleOrNull() ?: 0.0
      val achieved = achieveStr.toDoubleOrNull() ?: 0.0

      if (party.isNotEmpty() && product.isNotEmpty()) {
        val normalizedDate = normalizeDate(date)
        entries.add(
          SalesEntry(
            id = UUID.randomUUID().toString(),
            party = party,
            date = normalizedDate,
            product = product,
            targetValue = target,
            achievementValue = achieved,
            notes = notes,
            createdAt = System.currentTimeMillis()
          )
        )
      }
    }
    return entries
  }

  private fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val sb = StringBuilder()
    var inQuotes = false

    var i = 0
    while (i < line.length) {
      val c = line[i]
      if (c == '"') {
        if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
          sb.append('"')
          i++
        } else {
          inQuotes = !inQuotes
        }
      } else if (c == ',' && !inQuotes) {
        result.add(sb.toString())
        sb.clear()
      } else {
        sb.append(c)
      }
      i++
    }
    result.add(sb.toString())
    return result
  }

  private fun normalizeDate(rawDate: String): String {
    // If format is DD/MM/YYYY or DD-MM-YYYY
    if (rawDate.contains("/")) {
      val parts = rawDate.split("/")
      if (parts.size == 3) {
        if (parts[2].length == 4) { // DD/MM/YYYY
          val day = parts[0].padStart(2, '0')
          val month = parts[1].padStart(2, '0')
          val year = parts[2]
          return "$year-$month-$day"
        }
      }
    }
    // If format is YYYY-MM-DD
    if (rawDate.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
      return rawDate
    }
    // Default fallback to today
    val now = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    return if (rawDate.isNotBlank()) rawDate else now
  }

  fun generateCsv(entries: List<SalesEntry>): String {
    val sb = StringBuilder()
    sb.append("Party,Date,Product,Target Value,Achievement Value,Notes\n")
    for (entry in entries) {
      sb.append("\"").append(entry.party.replace("\"", "\"\"")).append("\",")
      sb.append(entry.date).append(",")
      sb.append("\"").append(entry.product.replace("\"", "\"\"")).append("\",")
      sb.append(entry.targetValue).append(",")
      sb.append(entry.achievementValue).append(",")
      sb.append("\"").append(entry.notes.replace("\"", "\"\"")).append("\"\n")
    }
    return sb.toString()
  }
}
