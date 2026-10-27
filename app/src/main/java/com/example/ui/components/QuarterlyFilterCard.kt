package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuarterlyFilterCard(
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
  var partyDropdownExpanded by remember { mutableStateOf(false) }
  var productDropdownExpanded by remember { mutableStateOf(false) }
  var partySearchText by remember { mutableStateOf("") }
  var productSearchText by remember { mutableStateOf("") }

  val hasActiveFilters = selectedYear != null || selectedQuarter != null || selectedParty != null || selectedProduct != null

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(24.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
              )
            }
          }
          Text(
            "QUARTERLY REPORT FILTERS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            color = MaterialTheme.colorScheme.secondary
          )
        }

        if (hasActiveFilters) {
          TextButton(
            onClick = onClearFilters,
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.FilterListOff, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reset", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 1. Quarter Selector Pills
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            Icons.Default.PieChart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp)
          )
          Text(
            "Quarter:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        val quartersList = listOf(
          null to "All Quarters",
          "Q1" to "Q1 (Jan-Mar)",
          "Q2" to "Q2 (Apr-Jun)",
          "Q3" to "Q3 (Jul-Sep)",
          "Q4" to "Q4 (Oct-Dec)"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          quartersList.forEach { (qKey, label) ->
            val isSelected = selectedQuarter == qKey
            FilterChip(
              selected = isSelected,
              onClick = { onSelectQuarter(qKey) },
              label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              shape = RoundedCornerShape(16.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
              )
            )
          }
        }
      }

      // 2. Year Selector Pills
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
          Text(
            "Financial Year:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = selectedYear == null,
            onClick = { onSelectYear(null) },
            label = { Text("All Time", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )

          availableYears.forEach { yr ->
            FilterChip(
              selected = selectedYear == yr,
              onClick = { onSelectYear(yr) },
              label = { Text(yr.toString(), fontWeight = FontWeight.SemiBold) },
              shape = RoundedCornerShape(16.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }
      }

      // 3. Dropdown Controls: Party & Product Filters
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Party Selector Button
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { partyDropdownExpanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  Icons.Default.Business,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedParty != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = selectedParty ?: "Party: All",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = if (selectedParty != null) FontWeight.Bold else FontWeight.Normal,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  color = if (selectedParty != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              }
              Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Party Dropdown Menu
          DropdownMenu(
            expanded = partyDropdownExpanded,
            onDismissRequest = {
              partyDropdownExpanded = false
              partySearchText = ""
            },
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .background(MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
              OutlinedTextField(
                value = partySearchText,
                onValueChange = { partySearchText = it },
                placeholder = { Text("Search party...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                  if (partySearchText.isNotEmpty()) {
                    IconButton(onClick = { partySearchText = "" }, modifier = Modifier.size(24.dp)) {
                      Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                    }
                  }
                },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp),
                shape = RoundedCornerShape(10.dp)
              )
            }

            DropdownMenuItem(
              text = { Text("All Parties (No Filter)", fontWeight = FontWeight.Bold) },
              onClick = {
                onSelectParty(null)
                partyDropdownExpanded = false
                partySearchText = ""
              }
            )

            val filteredParties = knownParties.filter {
              it.contains(partySearchText, ignoreCase = true)
            }

            filteredParties.forEach { party ->
              DropdownMenuItem(
                text = {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(party, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (selectedParty == party) {
                      Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(8.dp)
                      ) {}
                    }
                  }
                },
                onClick = {
                  onSelectParty(party)
                  partyDropdownExpanded = false
                  partySearchText = ""
                }
              )
            }
          }
        }

        // Product Selector Button
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { productDropdownExpanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  Icons.Default.Category,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedProduct != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = selectedProduct ?: "Product: All",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = if (selectedProduct != null) FontWeight.Bold else FontWeight.Normal,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  color = if (selectedProduct != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
              }
              Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Product Dropdown Menu
          DropdownMenu(
            expanded = productDropdownExpanded,
            onDismissRequest = {
              productDropdownExpanded = false
              productSearchText = ""
            },
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .background(MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
              OutlinedTextField(
                value = productSearchText,
                onValueChange = { productSearchText = it },
                placeholder = { Text("Search product...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                  if (productSearchText.isNotEmpty()) {
                    IconButton(onClick = { productSearchText = "" }, modifier = Modifier.size(24.dp)) {
                      Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                    }
                  }
                },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp),
                shape = RoundedCornerShape(10.dp)
              )
            }

            DropdownMenuItem(
              text = { Text("All Products (No Filter)", fontWeight = FontWeight.Bold) },
              onClick = {
                onSelectProduct(null)
                productDropdownExpanded = false
                productSearchText = ""
              }
            )

            val filteredProducts = knownProducts.filter {
              it.contains(productSearchText, ignoreCase = true)
            }

            filteredProducts.forEach { product ->
              DropdownMenuItem(
                text = {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(product, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (selectedProduct == product) {
                      Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(8.dp)
                      ) {}
                    }
                  }
                },
                onClick = {
                  onSelectProduct(product)
                  productDropdownExpanded = false
                  productSearchText = ""
                }
              )
            }
          }
        }
      }

      // 4. Active Filters summary tags
      AnimatedVisibility(visible = selectedQuarter != null || selectedParty != null || selectedProduct != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "Active Filters:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          selectedQuarter?.let { quarter ->
            InputChip(
              selected = true,
              onClick = { onSelectQuarter(null) },
              label = { Text("Quarter: $quarter", style = MaterialTheme.typography.labelSmall) },
              trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear quarter", modifier = Modifier.size(12.dp)) },
              shape = RoundedCornerShape(12.dp),
              colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
              )
            )
          }

          selectedParty?.let { party ->
            InputChip(
              selected = true,
              onClick = { onSelectParty(null) },
              label = { Text("Party: $party", style = MaterialTheme.typography.labelSmall) },
              trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear party", modifier = Modifier.size(12.dp)) },
              shape = RoundedCornerShape(12.dp),
              colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }

          selectedProduct?.let { product ->
            InputChip(
              selected = true,
              onClick = { onSelectProduct(null) },
              label = { Text("Product: $product", style = MaterialTheme.typography.labelSmall) },
              trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear product", modifier = Modifier.size(12.dp)) },
              shape = RoundedCornerShape(12.dp),
              colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
              )
            )
          }
        }
      }
    }
  }
}
