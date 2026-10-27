package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SalesEntry
import com.example.ui.components.ChartColors
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.MetricCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.utils.CurrencyFormatter

@Composable
fun EntriesScreen(
  entries: List<SalesEntry>,
  totalTarget: Double,
  totalAchieved: Double,
  achievementPct: Int,
  totalEntriesCount: Int,
  activeParties: Set<String>,
  selectedMonth: String?,
  selectedYear: Int?,
  selectedProduct: String?,
  searchQuery: String,
  availableYears: List<Int>,
  knownProducts: List<String>,
  knownParties: List<String>,
  onSearchChange: (String) -> Unit,
  onAddPartyFilter: (String) -> Unit,
  onRemovePartyFilter: (String) -> Unit,
  onSelectMonth: (String?) -> Unit,
  onSelectYear: (Int?) -> Unit,
  onSelectProduct: (String?) -> Unit,
  onClearFilters: () -> Unit,
  onEditEntry: (SalesEntry) -> Unit,
  onDeleteEntry: (String) -> Unit,
  onAddNewEntry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var entryToDelete by remember { mutableStateOf<SalesEntry?>(null) }
  var monthDropdownExpanded by remember { mutableStateOf(false) }
  var yearDropdownExpanded by remember { mutableStateOf(false) }
  var productDropdownExpanded by remember { mutableStateOf(false) }
  var partyDropdownExpanded by remember { mutableStateOf(false) }

  // Group entries by party
  val partyGroups = remember(entries) {
    entries.groupBy { it.party }.toList()
  }

  // Track expanded state for party cards
  var expandedParties by remember { mutableStateOf(setOf<String>()) }

  val allExpanded = partyGroups.isNotEmpty() && partyGroups.all { expandedParties.contains(it.first) }

  val monthsList = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Top Summary Stat Cards Grid
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      MetricCard(
        title = "Target",
        value = CurrencyFormatter.formatCompact(totalTarget),
        subtitle = "Total planned",
        valueColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Achieved",
        value = CurrencyFormatter.formatCompact(totalAchieved),
        subtitle = "${CurrencyFormatter.formatPercentage(totalTarget, totalAchieved)} rate",
        valueColor = EmeraldSuccess,
        badgeText = CurrencyFormatter.formatPercentage(totalTarget, totalAchieved),
        badgeColor = if (totalTarget > 0 && (totalAchieved / totalTarget) >= 1.0) EmeraldSuccess else AmberWarning,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Parties",
        value = partyGroups.size.toString(),
        subtitle = "$totalEntriesCount Records",
        modifier = Modifier.weight(0.8f)
      )
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      placeholder = { Text("Search party, product, notes...", style = MaterialTheme.typography.bodyMedium) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("entries_search_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Quick Filter Buttons Row (Month, Year, Product, Party)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Month dropdown filter
      Box(modifier = Modifier.weight(1f)) {
        OutlinedCard(
          onClick = { monthDropdownExpanded = true },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              selectedMonth ?: "Month",
              style = MaterialTheme.typography.labelMedium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
        DropdownMenu(
          expanded = monthDropdownExpanded,
          onDismissRequest = { monthDropdownExpanded = false }
        ) {
          DropdownMenuItem(
            text = { Text("All Months") },
            onClick = {
              onSelectMonth(null)
              monthDropdownExpanded = false
            }
          )
          monthsList.forEach { m ->
            DropdownMenuItem(
              text = { Text(m) },
              onClick = {
                onSelectMonth(m)
                monthDropdownExpanded = false
              }
            )
          }
        }
      }

      // Year dropdown filter
      Box(modifier = Modifier.weight(0.9f)) {
        OutlinedCard(
          onClick = { yearDropdownExpanded = true },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              selectedYear?.toString() ?: "Year",
              style = MaterialTheme.typography.labelMedium,
              maxLines = 1
            )
            Icon(Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
        DropdownMenu(
          expanded = yearDropdownExpanded,
          onDismissRequest = { yearDropdownExpanded = false }
        ) {
          DropdownMenuItem(
            text = { Text("All Years") },
            onClick = {
              onSelectYear(null)
              yearDropdownExpanded = false
            }
          )
          availableYears.forEach { yr ->
            DropdownMenuItem(
              text = { Text(yr.toString()) },
              onClick = {
                onSelectYear(yr)
                yearDropdownExpanded = false
              }
            )
          }
        }
      }

      // Product dropdown filter
      Box(modifier = Modifier.weight(1f)) {
        OutlinedCard(
          onClick = { productDropdownExpanded = true },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              selectedProduct ?: "Product",
              style = MaterialTheme.typography.labelMedium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
        DropdownMenu(
          expanded = productDropdownExpanded,
          onDismissRequest = { productDropdownExpanded = false }
        ) {
          DropdownMenuItem(
            text = { Text("All Products") },
            onClick = {
              onSelectProduct(null)
              productDropdownExpanded = false
            }
          )
          knownProducts.forEach { p ->
            DropdownMenuItem(
              text = { Text(p) },
              onClick = {
                onSelectProduct(p)
                productDropdownExpanded = false
              }
            )
          }
        }
      }

      // Party dropdown filter
      if (knownParties.isNotEmpty()) {
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { partyDropdownExpanded = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                if (activeParties.isNotEmpty()) "${activeParties.size} Party" else "+ Party",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp))
            }
          }
          DropdownMenu(
            expanded = partyDropdownExpanded,
            onDismissRequest = { partyDropdownExpanded = false }
          ) {
            knownParties.forEach { p ->
              val isSelected = activeParties.contains(p)
              DropdownMenuItem(
                text = { Text(if (isSelected) "✓ $p" else p) },
                onClick = {
                  if (isSelected) onRemovePartyFilter(p) else onAddPartyFilter(p)
                  partyDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    }

    // Active filter chips
    FilterChipsRow(
      activeParties = activeParties,
      selectedMonth = selectedMonth,
      selectedYear = selectedYear,
      selectedProduct = selectedProduct,
      searchQuery = searchQuery,
      onRemoveParty = onRemovePartyFilter,
      onRemoveMonth = { onSelectMonth(null) },
      onRemoveYear = { onSelectYear(null) },
      onRemoveProduct = { onSelectProduct(null) },
      onRemoveSearch = { onSearchChange("") },
      onClearAll = onClearFilters
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Records List Header with Expand All / Collapse All controls
    if (partyGroups.isNotEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "${partyGroups.size} ${if (partyGroups.size == 1) "Party Card" else "Party Cards"} (${entries.size} total entries)",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.SemiBold
        )

        TextButton(
          onClick = {
            expandedParties = if (allExpanded) {
              emptySet()
            } else {
              partyGroups.map { it.first }.toSet()
            }
          },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(
            if (allExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            if (allExpanded) "Collapse All" else "Expand All",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // Records List
    if (partyGroups.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            shape = CircleShape,
            modifier = Modifier.size(64.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            "No sales entries found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Try adjusting your active filters or add a new target entry.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onAddNewEntry,
            modifier = Modifier.testTag("empty_add_entry_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Sales Target")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(partyGroups, key = { it.first }) { (partyName, partyEntries) ->
          val isExpanded = expandedParties.contains(partyName)

          PartyGroupCard(
            partyName = partyName,
            partyEntries = partyEntries,
            isExpanded = isExpanded,
            onToggleExpand = {
              expandedParties = if (isExpanded) {
                expandedParties - partyName
              } else {
                expandedParties + partyName
              }
            },
            onEditEntry = onEditEntry,
            onDeleteEntry = { entryToDelete = it },
            modifier = Modifier.testTag("party_card_$partyName")
          )
        }
      }
    }
  }

  // Delete confirmation dialog
  entryToDelete?.let { entry ->
    AlertDialog(
      onDismissRequest = { entryToDelete = null },
      shape = RoundedCornerShape(16.dp),
      title = { Text("Confirm Deletion", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Are you sure you want to delete this sales entry?")
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Party: ${entry.party}", fontWeight = FontWeight.SemiBold)
              Text("Product: ${entry.product}", style = MaterialTheme.typography.bodySmall)
              Text("Date: ${entry.formattedDisplayDate}", style = MaterialTheme.typography.bodySmall)
              Text("Target: ${CurrencyFormatter.formatRupees(entry.targetValue)}", style = MaterialTheme.typography.bodySmall)
              Text("Achieved: ${CurrencyFormatter.formatRupees(entry.achievementValue)}", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteEntry(entry.id)
            entryToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { entryToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

/**
 * Unified Party Card aggregating all products/entries for a single client/party.
 * Tapping expands to reveal all product-wise Target vs Achievement with percentages.
 */
@Composable
fun PartyGroupCard(
  partyName: String,
  partyEntries: List<SalesEntry>,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onEditEntry: (SalesEntry) -> Unit,
  onDeleteEntry: (SalesEntry) -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalPartyTarget = partyEntries.sumOf { it.targetValue }
  val totalPartyAchieved = partyEntries.sumOf { it.achievementValue }
  val partyPct = if (totalPartyTarget > 0) Math.round((totalPartyAchieved / totalPartyTarget) * 100).toInt() else if (totalPartyAchieved > 0) 100 else 0
  val isComplete = partyPct >= 100
  val isPositive = totalPartyAchieved >= totalPartyTarget
  val distinctProductsCount = partyEntries.map { it.product }.distinct().size

  val rotationAngle by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "chevronRotation"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(20.dp),
    border = if (isExpanded) {
      androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    } else {
      CardDefaults.outlinedCardBorder()
    }
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // 1. Party Summary Header (Tappable to expand / collapse)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() }
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Party Icon Badge
            Surface(
              color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.size(42.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.Business,
                  contentDescription = null,
                  tint = if (isExpanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                partyName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                "$distinctProductsCount ${if (distinctProductsCount == 1) "Product" else "Products"} • ${partyEntries.size} ${if (partyEntries.size == 1) "Entry" else "Entries"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Percentage Badge + Chevron Button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = if (isComplete) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                "$partyPct%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isComplete) EmeraldSuccess else AmberWarning,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Icon(
              Icons.Default.KeyboardArrowDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Aggregate Progress Bar
        val overallProgress = if (totalPartyTarget > 0) (totalPartyAchieved / totalPartyTarget).toFloat().coerceIn(0f, 1f) else if (totalPartyAchieved > 0) 1f else 0f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(overallProgress)
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(if (isComplete) EmeraldSuccess else MaterialTheme.colorScheme.primary)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Target vs Achieved Summary Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column {
              Text("Total Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(
                CurrencyFormatter.formatRupees(totalPartyTarget),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
            }

            Column {
              Text("Total Achieved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(
                CurrencyFormatter.formatRupees(totalPartyAchieved),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Text(
            text = if (isExpanded) "Tap to collapse" else "Tap to view products",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // 2. Expandable Products List Section
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 4.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "PRODUCT-WISE BREAKDOWN",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              "${partyEntries.size} entries",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Individual Product Entry Cards
          partyEntries.forEachIndexed { index, entry ->
            ProductEntryItemCard(
              entry = entry,
              colorIndex = index,
              onEdit = { onEditEntry(entry) },
              onDelete = { onDeleteEntry(entry) }
            )
          }
        }
      }
    }
  }
}

/**
 * Product-wise individual target vs achievement card nested under the Party Card.
 */
@Composable
fun ProductEntryItemCard(
  entry: SalesEntry,
  colorIndex: Int,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val pct = entry.achievementPercentage
  val isComplete = pct >= 100
  val isPositive = entry.achievementValue >= entry.targetValue
  val tagColor = ChartColors.getOrNull(colorIndex % ChartColors.size) ?: MaterialTheme.colorScheme.primary

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(14.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header: Product Name + Date Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(tagColor)
          )
          Text(
            entry.product,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            entry.formattedDisplayDate,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Progress bar for this product
      val progress = if (entry.targetValue > 0) (entry.achievementValue / entry.targetValue).toFloat().coerceIn(0f, 1f) else if (entry.achievementValue > 0) 1f else 0f
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(RoundedCornerShape(2.5.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(progress)
            .height(5.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .background(if (isComplete) EmeraldSuccess else tagColor)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Target vs Achieved Row + % Pill + Edit/Delete Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
          Column {
            Text("Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              CurrencyFormatter.formatRupees(entry.targetValue),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold
            )
          }

          Column {
            Text("Achieved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              CurrencyFormatter.formatRupees(entry.achievementValue),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = if (entry.achievementValue < 0) RoseDanger else if (isPositive) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
            )
          }

          Column {
            Text("Rate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Surface(
              color = if (isComplete) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                CurrencyFormatter.formatPercentage(entry.targetValue, entry.achievementValue),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isComplete) EmeraldSuccess else AmberWarning,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }
        }

        // Action Buttons: Edit and Delete
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(
              Icons.Default.Edit,
              contentDescription = "Edit",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Delete",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Notes if present
      if (entry.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            Icons.Default.Notes,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            entry.notes,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}
