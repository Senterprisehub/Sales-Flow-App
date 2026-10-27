package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserManualDialog(
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(28.dp),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "Sales Flow Manual",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            "Developed By S.R Software Studio",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        ManualSection(
          icon = Icons.Default.AddCircle,
          title = "1. Target Month/Year & Achievement Dates",
          description = "When adding a sales record, the Target Month and Year are automatically fetched from current system time and can be edited. The specific date is collected for the Achievement Value. Target and Achievement values can both be ₹0."
        )

        ManualSection(
          icon = Icons.Default.PlaylistAdd,
          title = "2. Multiple Achievements per Party & Product",
          description = "You can record multiple achievement entries for the same client and product (e.g. multi-phase payments, recurring milestones, or split deliveries). All values automatically aggregate across yearly analysis, product breakdowns, and monthly charts."
        )

        ManualSection(
          icon = Icons.Default.Analytics,
          title = "3. Total Yearly Analysis Dashboard",
          description = "Switch to the 'Yearly Analysis' tab to view comprehensive annual metrics, interactive 12-month bar charts with tap inspection, quarterly performance quadrants (Q1-Q4), and year-over-year summaries."
        )

        ManualSection(
          icon = Icons.Default.BarChart,
          title = "4. Product & Party Insights",
          description = "Explore the 'Insights' tab for custom interactive donut distribution charts, revenue breakdown percentages, and ranked leaderboards for top clients."
        )

        ManualSection(
          icon = Icons.Default.FilterList,
          title = "5. Multi-Filter & Search",
          description = "Filter by party names, months, years, or products, and perform full-text instant search across parties, products, and notes. Tap filter chips to easily remove specific parameters."
        )

        ManualSection(
          icon = Icons.Default.ImportExport,
          title = "6. CSV Sync & Export",
          description = "Export filtered or complete datasets to standard CSV files to share via email, drive, or other apps. Import existing CSV sheets with automatic column detection."
        )

        ManualSection(
          icon = Icons.Default.RestartAlt,
          title = "7. Local Database & Reset",
          description = "All sales data is securely persisted on-device using Room. You can safely clear all data or delete obsolete party/product entries anytime."
        )
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Got it")
      }
    }
  )
}

@Composable
private fun ManualSection(
  icon: ImageVector,
  title: String,
  description: String,
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    shape = RoundedCornerShape(10.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 2.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
