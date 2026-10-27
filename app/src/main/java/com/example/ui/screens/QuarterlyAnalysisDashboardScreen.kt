package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChartColors
import com.example.ui.components.MetricCard
import com.example.ui.components.QuarterlyFilterCard
import com.example.ui.components.QuarterlyPerformanceGrid
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.viewmodel.QuarterlyDetailReport
import com.example.utils.CurrencyFormatter

@Composable
fun QuarterlyAnalysisDashboardScreen(
  report: QuarterlyDetailReport,
  availableYears: List<Int>,
  selectedYear: Int?,
  selectedQuarter: String?,
  knownParties: List<String>,
  selectedParty: String?,
  knownProducts: List<String>,
  selectedProduct: String?,
  onSelectYear: (Int?) -> Unit,
  onSelectQuarter: (String?) -> Unit,
  onSelectParty: (String?) -> Unit,
  onSelectProduct: (String?) -> Unit,
  onClearFilters: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isSurplus = report.variance >= 0
  val quarterTitle = when (report.selectedQuarter) {
    "Q1" -> "Q1 Performance (Jan - Mar)"
    "Q2" -> "Q2 Performance (Apr - Jun)"
    "Q3" -> "Q3 Performance (Jul - Sep)"
    "Q4" -> "Q4 Performance (Oct - Dec)"
    else -> "Quarterly Report (All Quarters)"
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("quarterly_analysis_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Filter Card (Quarter, Year, Party Wise, Product Wise)
    QuarterlyFilterCard(
      availableYears = availableYears,
      selectedYear = selectedYear,
      selectedQuarter = selectedQuarter,
      knownParties = knownParties,
      selectedParty = selectedParty,
      knownProducts = knownProducts,
      selectedProduct = selectedProduct,
      onSelectYear = onSelectYear,
      onSelectQuarter = onSelectQuarter,
      onSelectParty = onSelectParty,
      onSelectProduct = onSelectProduct,
      onClearFilters = onClearFilters
    )

    // 2. Quarterly Hero Overview Card
    QuarterlyHeroBanner(
      report = report,
      quarterTitle = quarterTitle,
      selectedQuarter = selectedQuarter,
      selectedParty = selectedParty,
      selectedProduct = selectedProduct,
      onClearQuarter = { onSelectQuarter(null) },
      onClearParty = { onSelectParty(null) },
      onClearProduct = { onSelectProduct(null) }
    )

    // 3. Key Financial Metrics 2x2 Grid
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricCard(
          title = "Quarter Target",
          value = CurrencyFormatter.formatRupees(report.totalTarget),
          subtitle = if (report.selectedQuarter != null) "${report.selectedQuarter} Target Quota" else "Annual Quota",
          valueColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Quarter Achieved",
          value = CurrencyFormatter.formatRupees(report.totalAchieved),
          subtitle = "${report.achievementPct}% Rate",
          valueColor = if (report.achievementPct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.onSurface,
          badgeText = "${report.achievementPct}%",
          badgeColor = if (report.achievementPct >= 100) EmeraldSuccess else AmberWarning,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricCard(
          title = if (isSurplus) "Quarter Surplus" else "Quarter Deficit",
          value = (if (isSurplus) "+ " else "- ") + CurrencyFormatter.formatRupees(Math.abs(report.variance)),
          subtitle = if (isSurplus) "Above Quota Target" else "Behind Quota Target",
          valueColor = if (isSurplus) EmeraldSuccess else RoseDanger,
          badgeText = if (isSurplus) "Surplus" else "Deficit",
          badgeColor = if (isSurplus) EmeraldSuccess else RoseDanger,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Avg Deal Size",
          value = CurrencyFormatter.formatRupees(report.avgDealSize),
          subtitle = "${report.totalEntries} sales recorded",
          valueColor = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 4. Interactive 4-Quarter Quadrant Matrix
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "Quarterly Breakdown (Q1 - Q4)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              if (selectedParty != null || selectedProduct != null) "Filtered by party/product" else "Tap a quadrant to deep-dive into that quarter",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.PieChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        QuarterlyPerformanceGrid(
          quarterlyMetrics = report.allQuarterMetrics,
          selectedQuarter = selectedQuarter,
          onSelectQuarter = onSelectQuarter
        )
      }
    }

    // 5. Party Wise Performance in Quarter Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "Party-Wise Performance",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              if (selectedParty != null) "Focusing on $selectedParty" else "Ranked party contributions for ${selectedQuarter ?: "the year"}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (report.topParties.isEmpty()) {
          Text(
            "No party sales records found for current filter selection.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp)
          )
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            report.topParties.forEachIndexed { idx, party ->
              val pct = party.percentage
              val maxAchieved = report.topParties.maxOfOrNull { it.achieved } ?: 1.0
              val progress = if (maxAchieved > 0) (party.achieved / maxAchieved).toFloat().coerceIn(0f, 1f) else 0f
              val isSelected = selectedParty?.equals(party.partyName, ignoreCase = true) == true

              Surface(
                onClick = {
                  if (isSelected) onSelectParty(null) else onSelectParty(party.partyName)
                },
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                          )
                        }
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          party.partyName,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                        Text(
                          "${party.entryCount} sales records • tap to filter",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        CurrencyFormatter.formatRupees(party.achieved),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        "$pct% (${CurrencyFormatter.formatCompact(party.target)} target)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pct >= 100) EmeraldSuccess else if (pct > 0) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp))
                      .background(MaterialTheme.colorScheme.surfaceVariant)
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.primary)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 6. Product Wise Performance in Quarter Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "Product-Wise Performance",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              if (selectedProduct != null) "Focusing on $selectedProduct" else "Revenue by product for ${selectedQuarter ?: "the year"}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Category,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (report.topProducts.isEmpty()) {
          Text(
            "No product sales records found for current filter selection.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp)
          )
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            report.topProducts.forEachIndexed { idx, product ->
              val pct = product.percentage
              val maxAchieved = report.topProducts.maxOfOrNull { it.achieved } ?: 1.0
              val progress = if (maxAchieved > 0) (product.achieved / maxAchieved).toFloat().coerceIn(0f, 1f) else 0f
              val itemColor = ChartColors.getOrNull(idx % ChartColors.size) ?: MaterialTheme.colorScheme.secondary
              val isSelected = selectedProduct?.equals(product.productName, ignoreCase = true) == true

              Surface(
                onClick = {
                  if (isSelected) onSelectProduct(null) else onSelectProduct(product.productName)
                },
                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary) else null
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Surface(
                        color = itemColor,
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                          )
                        }
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          product.productName,
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                        Text(
                          "${product.entryCount} sales records • tap to filter",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        CurrencyFormatter.formatRupees(product.achieved),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        "$pct% (${CurrencyFormatter.formatCompact(product.target)} target)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pct >= 100) EmeraldSuccess else if (pct > 0) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp))
                      .background(MaterialTheme.colorScheme.surfaceVariant)
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(itemColor)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 7. Quarter's Month-by-Month Detailed Table
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          if (selectedQuarter != null) "$selectedQuarter Monthly Target vs Achieved" else "Quarterly Months Schedule",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          "Month-by-month progress breakdown",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Table Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("MONTH", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
          Text("TARGET", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
          Text("ACHIEVED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
          Text("RATE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (report.quarterMonthlyMetrics.isEmpty()) {
          Text(
            "No monthly data found.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
          )
        } else {
          report.quarterMonthlyMetrics.forEach { month ->
            val pct = month.percentage
            val isPositive = month.achieved >= month.target
            val progress = if (month.target > 0) (month.achieved / month.target).toFloat().coerceIn(0f, 1f) else 0f

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  month.monthName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.weight(1.2f)
                )
                Text(
                  CurrencyFormatter.formatCompact(month.target),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.weight(1.3f)
                )
                Text(
                  CurrencyFormatter.formatCompact(month.achieved),
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isPositive) EmeraldSuccess else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1.3f)
                )
                Surface(
                  color = if (pct >= 100) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(0.8f)
                ) {
                  Text(
                    "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (pct >= 100) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.primary)
                )
              }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          }
        }
      }
    }
  }
}

@Composable
private fun QuarterlyHeroBanner(
  report: QuarterlyDetailReport,
  quarterTitle: String,
  selectedQuarter: String?,
  selectedParty: String?,
  selectedProduct: String?,
  onClearQuarter: () -> Unit,
  onClearParty: () -> Unit,
  onClearProduct: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isSurplus = report.variance >= 0
  val primaryGradient = Brush.horizontalGradient(
    colors = listOf(
      Color(0xFF311B92),
      Color(0xFF512DA8),
      Color(0xFF673AB7)
    )
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(28.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(primaryGradient)
        .padding(20.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Title row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Surface(
              color = Color.White.copy(alpha = 0.18f),
              shape = RoundedCornerShape(20.dp)
            ) {
              Text(
                text = if (report.year != null) "${report.year} FISCAL REPORT" else "ALL TIME QUARTERLY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              quarterTitle,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          // Achievement Badge Gauge
          Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(56.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  "${report.achievementPct}%",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (report.achievementPct >= 100) Color(0xFF69F0AE) else Color(0xFFFFD54F)
                )
                Text(
                  "RATE",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White.copy(alpha = 0.8f)
                )
              }
            }
          }
        }

        // Active filter tags if any
        if (selectedQuarter != null || selectedParty != null || selectedProduct != null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Filtered by:",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.8f)
            )

            selectedQuarter?.let {
              Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.clickable { onClearQuarter() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("Quarter: $it", style = MaterialTheme.typography.labelSmall, color = Color.White)
                  Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
              }
            }

            selectedParty?.let {
              Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.clickable { onClearParty() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("Party: $it", style = MaterialTheme.typography.labelSmall, color = Color.White)
                  Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
              }
            }

            selectedProduct?.let {
              Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.clickable { onClearProduct() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text("Product: $it", style = MaterialTheme.typography.labelSmall, color = Color.White)
                  Icon(Icons.Default.Clear, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
              }
            }
          }
        }

        // Two-column overview: Achieved vs Target
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              "TOTAL ACHIEVED",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.75f),
              letterSpacing = 0.5.sp
            )
            Text(
              CurrencyFormatter.formatRupees(report.totalAchieved),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              "TARGET QUOTA",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.75f),
              letterSpacing = 0.5.sp
            )
            Text(
              CurrencyFormatter.formatRupees(report.totalTarget),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
        }

        // Progress Bar
        val progress = if (report.totalTarget > 0) (report.totalAchieved / report.totalTarget).toFloat().coerceIn(0f, 1f) else 0f
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp))
              .background(Color.White.copy(alpha = 0.2f))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(progress)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (report.achievementPct >= 100) Color(0xFF69F0AE) else Color(0xFFFFD54F))
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              "${report.totalEntries} Total Sales Entries",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.75f)
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(
                if (isSurplus) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (isSurplus) Color(0xFF69F0AE) else Color(0xFFFF8A80),
                modifier = Modifier.size(12.dp)
              )
              Text(
                (if (isSurplus) "+" else "-") + CurrencyFormatter.formatCompact(Math.abs(report.variance)) + (if (isSurplus) " Surplus" else " Deficit"),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSurplus) Color(0xFF69F0AE) else Color(0xFFFF8A80)
              )
            }
          }
        }
      }
    }
  }
}
