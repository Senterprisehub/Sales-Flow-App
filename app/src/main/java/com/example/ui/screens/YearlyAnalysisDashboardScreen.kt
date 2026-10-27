package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChartColors
import com.example.ui.components.MetricCard
import com.example.ui.components.MonthlySalesBarChart
import com.example.ui.components.QuarterlyPerformanceGrid
import com.example.ui.components.YearlyFilterCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.viewmodel.QuarterlyDetailReport
import com.example.ui.viewmodel.YearlySummary
import com.example.utils.CurrencyFormatter

@Composable
fun YearlyAnalysisDashboardScreen(
  summary: YearlySummary,
  quarterlyReport: QuarterlyDetailReport,
  availableYears: List<Int>,
  selectedYear: Int?,
  knownParties: List<String>,
  selectedParty: String?,
  knownProducts: List<String>,
  selectedProduct: String?,
  selectedQuarter: String?,
  selectedQuarterlyParty: String?,
  selectedQuarterlyProduct: String?,
  onSelectYear: (Int?) -> Unit,
  onSelectParty: (String?) -> Unit,
  onSelectProduct: (String?) -> Unit,
  onClearFilters: () -> Unit,
  onSelectQuarter: (String?) -> Unit,
  onSelectQuarterlyParty: (String?) -> Unit,
  onSelectQuarterlyProduct: (String?) -> Unit,
  onClearQuarterlyFilters: () -> Unit,
  modifier: Modifier = Modifier,
) {
  // Sub-tab: 0 = Yearly Analysis, 1 = Quarterly Report
  var activeSubTab by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // Top Switcher Segment between Yearly Analysis & Quarterly Report
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      TabRow(
        selectedTabIndex = activeSubTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
            color = MaterialTheme.colorScheme.primary
          )
        }
      ) {
        Tab(
          selected = activeSubTab == 0,
          onClick = { activeSubTab = 0 },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
              Text("Yearly Analysis", fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal)
            }
          }
        )
        Tab(
          selected = activeSubTab == 1,
          onClick = { activeSubTab = 1 },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(18.dp))
              Text("Quarterly Report", fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal)
            }
          }
        )
      }
    }

    if (activeSubTab == 1) {
      QuarterlyAnalysisDashboardScreen(
        report = quarterlyReport,
        availableYears = availableYears,
        selectedYear = selectedYear,
        selectedQuarter = selectedQuarter,
        knownParties = knownParties,
        selectedParty = selectedQuarterlyParty,
        knownProducts = knownProducts,
        selectedProduct = selectedQuarterlyProduct,
        onSelectYear = onSelectYear,
        onSelectQuarter = onSelectQuarter,
        onSelectParty = onSelectQuarterlyParty,
        onSelectProduct = onSelectQuarterlyProduct,
        onClearFilters = onClearQuarterlyFilters
      )
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Interactive Filter Card for Year, Party-Wise, and Product-Wise Analysis
        YearlyFilterCard(
          availableYears = availableYears,
          selectedYear = selectedYear,
          knownParties = knownParties,
          selectedParty = selectedParty,
          knownProducts = knownProducts,
          selectedProduct = selectedProduct,
          onSelectYear = onSelectYear,
          onSelectParty = onSelectParty,
          onSelectProduct = onSelectProduct,
          onClearFilters = onClearFilters
        )

    // Hero Performance Card with Active Scope Label
    YearlyHeroBanner(
      summary = summary,
      selectedParty = selectedParty,
      selectedProduct = selectedProduct
    )

    // Key Metric 4-Tile Grid
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      MetricCard(
        title = "Total Target",
        value = CurrencyFormatter.formatCompact(summary.totalTarget),
        subtitle = if (selectedParty != null || selectedProduct != null) "Filtered quota" else "Annual quota",
        valueColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Total Achieved",
        value = CurrencyFormatter.formatCompact(summary.totalAchieved),
        subtitle = "${CurrencyFormatter.formatPercentage(summary.totalTarget, summary.totalAchieved)} target met",
        valueColor = EmeraldSuccess,
        modifier = Modifier.weight(1f)
      )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      val isSurplus = summary.variance >= 0
      MetricCard(
        title = if (isSurplus) "Surplus" else "Shortfall",
        value = CurrencyFormatter.formatCompact(Math.abs(summary.variance)),
        subtitle = if (isSurplus) "Above target" else "Below target",
        valueColor = if (isSurplus) EmeraldSuccess else RoseDanger,
        badgeText = if (isSurplus) "+Surplus" else "-Shortfall",
        badgeColor = if (isSurplus) EmeraldSuccess else RoseDanger,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Avg Deal Size",
        value = CurrencyFormatter.formatCompact(summary.avgDealSize),
        subtitle = "${summary.totalEntries} entries logged",
        modifier = Modifier.weight(1f)
      )
    }

    // Interactive Monthly Performance Bar Chart
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
              "Monthly Performance Chart",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              if (selectedParty != null || selectedProduct != null) "Target vs Achievement for active filters"
              else "Target vs Achievement across all 12 months",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.ShowChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MonthlySalesBarChart(
          monthlyMetrics = summary.monthlyMetrics
        )
      }
    }

    // Party Wise Analysis Card (Detailed Leaderboard & Progress)
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
              if (selectedParty != null) "Focused on party: $selectedParty" else "Client & distributor sales quota breakdown",
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
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (summary.topParties.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "No party records matching selected criteria",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            summary.topParties.forEachIndexed { idx, party ->
              val pct = party.percentage
              val isSelected = selectedParty != null && party.partyName.equals(selectedParty, ignoreCase = true)
              val progress = if (party.target > 0) (party.achieved / party.target).toFloat().coerceIn(0f, 1f) else if (party.achieved > 0) 1f else 0f

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  )
                  .clickable {
                    if (selectedParty == party.partyName) onSelectParty(null) else onSelectParty(party.partyName)
                  }
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                        "${party.entryCount} sales records",
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
                      "${CurrencyFormatter.formatPercentage(party.target, party.achieved)} (${CurrencyFormatter.formatCompact(party.target)} target)",
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

    // Product Wise Analysis Card (Detailed Performance & Comparison)
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
              if (selectedProduct != null) "Focused on product: $selectedProduct" else "Revenue by product and service category",
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
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (summary.topProducts.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "No product records matching selected criteria",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            summary.topProducts.forEachIndexed { idx, prod ->
              val pct = prod.percentage
              val isSelected = selectedProduct != null && prod.productName.equals(selectedProduct, ignoreCase = true)
              val progress = if (prod.target > 0) (prod.achieved / prod.target).toFloat().coerceIn(0f, 1f) else if (prod.achieved > 0) 1f else 0f
              val itemColor = ChartColors[idx % ChartColors.size]

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  )
                  .clickable {
                    if (selectedProduct == prod.productName) onSelectProduct(null) else onSelectProduct(prod.productName)
                  }
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                      modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        prod.productName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        "${prod.entryCount} sales records",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      CurrencyFormatter.formatRupees(prod.achieved),
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Bold,
                      color = if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      "${CurrencyFormatter.formatPercentage(prod.target, prod.achieved)} (${CurrencyFormatter.formatCompact(prod.target)} target)",
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

    // Quarterly Performance Quadrants (Q1-Q4)
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
              "Tap any quarter for deep-dive report",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          FilledTonalButton(
            onClick = { activeSubTab = 1 },
            shape = RoundedCornerShape(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text("Full Report", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        QuarterlyPerformanceGrid(
          quarterlyMetrics = summary.quarterlyMetrics,
          onSelectQuarter = { q ->
            onSelectQuarter(q)
            activeSubTab = 1
          }
        )
      }
    }

    // Performance Highlights & Milestones
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          "Yearly Highlights & Milestones",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        // Best Month
        HighlightRow(
          icon = Icons.Default.CalendarMonth,
          iconColor = IndigoPrimary,
          title = "Best Performing Month",
          value = summary.bestMonth?.let { "${it.monthName}: ${CurrencyFormatter.formatRupees(it.achieved)} (${CurrencyFormatter.formatPercentage(it.target, it.achieved)})" } ?: "No data yet"
        )

        // Top Client / Party
        HighlightRow(
          icon = Icons.Default.EmojiEvents,
          iconColor = EmeraldSuccess,
          title = "Top Revenue Party",
          value = summary.bestParty?.let { "${it.partyName}: ${CurrencyFormatter.formatRupees(it.achieved)} (${it.entryCount} entries)" } ?: "No data yet"
        )

        // Leading Product
        HighlightRow(
          icon = Icons.Default.LocalOffer,
          iconColor = AmberWarning,
          title = "Leading Product / Service",
          value = summary.topProduct?.let { "${it.productName}: ${CurrencyFormatter.formatRupees(it.achieved)}" } ?: "No data yet"
        )
      }
    }

    // Monthly Target vs Achieved Detailed Table List
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(28.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          "Monthly Target vs Achieved Schedule",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          "Detailed tabular month-by-month analysis",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        summary.monthlyMetrics.forEach { metric ->
          val pct = metric.percentage
          val hasData = metric.target > 0 || metric.achieved > 0
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.width(90.dp)) {
              Text(
                metric.monthName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                "${metric.entryCount} entries",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Progress bar
            val prog = if (metric.target > 0) (metric.achieved / metric.target).toFloat().coerceIn(0f, 1f) else if (metric.achieved > 0) 1f else 0f
            Box(
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
              if (hasData) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(prog)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (pct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.primary)
                )
              }
            }

            // Target / Achieved Values
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(110.dp)) {
              Text(
                "${CurrencyFormatter.formatCompact(metric.achieved)} / ${CurrencyFormatter.formatCompact(metric.target)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
              )
              Text(
                "${CurrencyFormatter.formatPercentage(metric.target, metric.achieved)} rate",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (pct >= 100) EmeraldSuccess else if (pct > 0) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun YearlyHeroBanner(
  summary: YearlySummary,
  selectedParty: String? = null,
  selectedProduct: String? = null,
) {
  val isPositive = summary.variance >= 0
  val rate = (summary.achievementPct / 100f).coerceIn(0f, 1f)

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    shape = RoundedCornerShape(28.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Surface(
          color = MaterialTheme.colorScheme.primary,
          shape = RoundedCornerShape(20.dp)
        ) {
          val yearLabel = if (summary.year != null) "YEAR ${summary.year}" else "ALL-TIME"
          val filterSuffix = when {
            selectedParty != null && selectedProduct != null -> " • $selectedParty / $selectedProduct"
            selectedParty != null -> " • $selectedParty"
            selectedProduct != null -> " • $selectedProduct"
            else -> ""
          }
          Text(
            text = "$yearLabel ANALYSIS$filterSuffix",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          CurrencyFormatter.formatRupees(summary.totalAchieved),
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
          "of ${CurrencyFormatter.formatRupees(summary.totalTarget)} Target",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
            contentDescription = null,
            tint = if (isPositive) EmeraldSuccess else RoseDanger,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${if (isPositive) "+₹" else "-₹"}${CurrencyFormatter.formatCompact(Math.abs(summary.variance))} variance",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) EmeraldSuccess else RoseDanger
          )
        }
      }

      // Circular Gauge
      Box(
        modifier = Modifier.size(92.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(
          progress = { 1f },
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
          strokeWidth = 8.dp,
        )
        CircularProgressIndicator(
          progress = { rate },
          modifier = Modifier.fillMaxSize(),
          color = if (summary.achievementPct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.primary,
          strokeWidth = 8.dp,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            CurrencyFormatter.formatPercentage(summary.totalTarget, summary.totalAchieved),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            "Achieved",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
      }
    }
  }
}

@Composable
private fun HighlightRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconColor: Color,
  title: String,
  value: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(12.dp),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.size(42.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
      }
    }
    Spacer(modifier = Modifier.width(14.dp))
    Column {
      Text(
        title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        value,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}
