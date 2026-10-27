package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.SalesEntry
import com.example.ui.screens.PartyGroupCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun sales_entry_card_screenshot() {
    val sampleEntry = SalesEntry(
      id = "test-1",
      party = "Acme Corp",
      date = "2026-03-15",
      product = "Enterprise Software",
      targetValue = 450000.0,
      achievementValue = 480000.0,
      notes = "Q1 Renewal & Add-on"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        PartyGroupCard(
          partyName = "Acme Corp",
          partyEntries = listOf(sampleEntry),
          isExpanded = true,
          onToggleExpand = {},
          onEditEntry = {},
          onDeleteEntry = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
