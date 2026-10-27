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
fun YearlyFilterCard(
  availableYears: List<Int>,
  selectedYear: Int?,
  knownParties: List<String>,
  selectedParty: String?,
  knownProducts: List<String>,
  selectedProduct: String?,
  onSelectYear: (Int?) -> Unit,
  onSelectParty: (String?) -> Unit,
  onSelectProduct: (String?) -> Unit,
  onClearFilters: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var partyDropdownExpanded by remember { mutableStateOf(false) }
  var productDropdownExpanded by remember { mutableStateOf(false) }
  var partySearchText by remember { mutableStateOf("") }
  var productSearchText by remember { mutableStateOf("") }

  val hasActiveFilters = selectedYear != null || selectedParty != null || selectedProduct != null

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
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
              )
            }
          }
          Text(
            "YEARLY ANALYSIS FILTERS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = MaterialTheme.colorScheme.primary
          )
        }

        if (hasActiveFilters) {
          TextButton(
            onClick = onClearFilters,
            modifier = Modifier.testTag("yearly_filter_clear_btn")
          ) {
            Icon(Icons.Default.FilterListOff, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reset", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Year Filter Horizontal Scroll Row
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          "Year:",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = selectedYear == null,
            onClick = { onSelectYear(null) },
            label = { Text("All Time", fontWeight = FontWeight.SemiBold) },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("yearly_chip_all_time")
          )

          availableYears.forEach { yr ->
            FilterChip(
              selected = selectedYear == yr,
              onClick = { onSelectYear(yr) },
              label = { Text(yr.toString(), fontWeight = FontWeight.SemiBold) },
              shape = RoundedCornerShape(20.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("yearly_chip_$yr")
            )
          }
        }
      }

      // Party Wise and Product Wise Selectors
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Party Wise Dropdown
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { partyDropdownExpanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("yearly_party_filter_btn"),
            colors = CardDefaults.outlinedCardColors(
              containerColor = if (selectedParty != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
              else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  "Party Wise",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  selectedParty ?: "All Parties",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
          }

          DropdownMenu(
            expanded = partyDropdownExpanded,
            onDismissRequest = { partyDropdownExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("All Parties (Total)", fontWeight = if (selectedParty == null) FontWeight.Bold else FontWeight.Normal) },
              onClick = {
                onSelectParty(null)
                partyDropdownExpanded = false
              }
            )
            knownParties.forEach { p ->
              DropdownMenuItem(
                text = {
                  Text(
                    p,
                    fontWeight = if (selectedParty == p) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedParty == p) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                },
                leadingIcon = {
                  Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                onClick = {
                  onSelectParty(p)
                  partyDropdownExpanded = false
                }
              )
            }
          }
        }

        // Product Wise Dropdown
        Box(modifier = Modifier.weight(1f)) {
          OutlinedCard(
            onClick = { productDropdownExpanded = true },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("yearly_product_filter_btn"),
            colors = CardDefaults.outlinedCardColors(
              containerColor = if (selectedProduct != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
              else MaterialTheme.colorScheme.surface
            )
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  "Product Wise",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  selectedProduct ?: "All Products",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
          }

          DropdownMenu(
            expanded = productDropdownExpanded,
            onDismissRequest = { productDropdownExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("All Products (Total)", fontWeight = if (selectedProduct == null) FontWeight.Bold else FontWeight.Normal) },
              onClick = {
                onSelectProduct(null)
                productDropdownExpanded = false
              }
            )
            knownProducts.forEach { prod ->
              DropdownMenuItem(
                text = {
                  Text(
                    prod,
                    fontWeight = if (selectedProduct == prod) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedProduct == prod) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                },
                leadingIcon = {
                  Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                onClick = {
                  onSelectProduct(prod)
                  productDropdownExpanded = false
                }
              )
            }
          }
        }
      }

      // Active Filter Badges
      if (selectedParty != null || selectedProduct != null) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          selectedParty?.let { p ->
            InputChip(
              selected = true,
              onClick = { onSelectParty(null) },
              label = { Text("Party: $p", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) },
              trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp)) },
              shape = RoundedCornerShape(16.dp),
              colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }

          selectedProduct?.let { prod ->
            InputChip(
              selected = true,
              onClick = { onSelectProduct(null) },
              label = { Text("Product: $prod", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) },
              trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp)) },
              shape = RoundedCornerShape(16.dp),
              colors = InputChipDefaults.inputChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }
      }
    }
  }
}
