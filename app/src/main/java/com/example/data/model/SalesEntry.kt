package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SalesEntry(
  val id: String,
  val party: String,
  val date: String, // Format: YYYY-MM-DD
  val product: String,
  val targetValue: Double,
  val achievementValue: Double,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
) {
  val achievementPercentage: Int
    get() = if (targetValue > 0.0) {
      ((achievementValue / targetValue) * 100).toInt()
    } else if (achievementValue > 0.0) {
      100
    } else {
      0
    }

  val year: Int
    get() = try {
      date.split("-")[0].toInt()
    } catch (e: Exception) {
      2026
    }

  val monthNumber: Int
    get() = try {
      date.split("-")[1].toInt()
    } catch (e: Exception) {
      1
    }

  val monthName: String
    get() = when (monthNumber) {
      1 -> "January"
      2 -> "February"
      3 -> "March"
      4 -> "April"
      5 -> "May"
      6 -> "June"
      7 -> "July"
      8 -> "August"
      9 -> "September"
      10 -> "October"
      11 -> "November"
      12 -> "December"
      else -> "Unknown"
    }

  val monthShortName: String
    get() = when (monthNumber) {
      1 -> "Jan"
      2 -> "Feb"
      3 -> "Mar"
      4 -> "Apr"
      5 -> "May"
      6 -> "Jun"
      7 -> "Jul"
      8 -> "Aug"
      9 -> "Sep"
      10 -> "Oct"
      11 -> "Nov"
      12 -> "Dec"
      else -> "—"
    }

  val quarter: String
    get() = when (monthNumber) {
      in 1..3 -> "Q1"
      in 4..6 -> "Q2"
      in 7..9 -> "Q3"
      in 10..12 -> "Q4"
      else -> "Q1"
    }

  val formattedDisplayDate: String
    get() = try {
      val parts = date.split("-")
      if (parts.size == 3) {
        "${parts[2]}/${parts[1]}/${parts[0]}"
      } else {
        date
      }
    } catch (e: Exception) {
      date
    }
}
