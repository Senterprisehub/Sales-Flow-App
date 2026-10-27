package com.example.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
  private val inLocale = Locale("en", "IN")
  private val inCurrencyFormat = NumberFormat.getCurrencyInstance(inLocale).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
  }

  fun formatRupees(amount: Double): String {
    return try {
      inCurrencyFormat.format(amount)
    } catch (e: Exception) {
      "₹${String.format(Locale.US, "%,.2f", amount)}"
    }
  }

  fun formatCompact(amount: Double): String {
    val absVal = Math.abs(amount)
    val sign = if (amount < 0) "-" else ""
    return when {
      absVal >= 10000000 -> "$sign₹${String.format(Locale.US, "%.2f", absVal / 10000000)}Cr"
      absVal >= 100000 -> "$sign₹${String.format(Locale.US, "%.2f", absVal / 100000)}L"
      absVal >= 1000 -> "$sign₹${String.format(Locale.US, "%.2f", absVal / 1000)}K"
      else -> "$sign₹${String.format(Locale.US, "%.2f", absVal)}"
    }
  }

  fun formatPercentage(target: Double, achieved: Double): String {
    if (target <= 0) return if (achieved > 0) "100.00%" else "0.00%"
    val pct = (achieved / target) * 100.0
    return String.format(Locale.US, "%.2f%%", pct)
  }

  fun formatPercentageValue(pct: Double): String {
    return String.format(Locale.US, "%.2f%%", pct)
  }
}

