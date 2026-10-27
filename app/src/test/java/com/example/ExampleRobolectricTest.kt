package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SalesEntry
import com.example.utils.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Sales Flow", appName)
  }

  @Test
  fun `test sales entry calculations`() {
    val entry = SalesEntry(
      id = "1",
      party = "Acme Corp",
      date = "2026-03-15",
      product = "Cloud Hosting",
      targetValue = 500000.0,
      achievementValue = 550000.0
    )

    assertEquals(110, entry.achievementPercentage)
    assertEquals(2026, entry.year)
    assertEquals(3, entry.monthNumber)
    assertEquals("March", entry.monthName)
    assertEquals("Q1", entry.quarter)
    assertEquals("15/03/2026", entry.formattedDisplayDate)
  }

  @Test
  fun `test currency formatting`() {
    assertEquals("100%", CurrencyFormatter.formatPercentage(500.0, 500.0))
    assertEquals("110%", CurrencyFormatter.formatPercentage(500.0, 550.0))
    assertEquals("50%", CurrencyFormatter.formatPercentage(1000.0, 500.0))
  }
}
