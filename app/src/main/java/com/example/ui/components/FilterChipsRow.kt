package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipsRow(
  activeParties: Set<String>,
  selectedMonth: String?,
  selectedYear: Int?,
  selectedProduct: String?,
  searchQuery: String,
  onRemoveParty: (String) -> Unit,
  onRemoveMonth: () -> Unit,
  onRemoveYear: () -> Unit,
  onRemoveProduct: () -> Unit,
  onRemoveSearch: () -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val hasAnyFilter = activeParties.isNotEmpty() || selectedMonth != null || selectedProduct != null || searchQuery.isNotBlank()

  if (!hasAnyFilter) return

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Clear all chip
    SuggestionChip(
      onClick = onClearAll,
      label = { Text("Clear All", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold) },
      colors = SuggestionChipDefaults.suggestionChipColors(
        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
        labelColor = MaterialTheme.colorScheme.error
      ),
      shape = RoundedCornerShape(20.dp)
    )

    // Party filters
    activeParties.forEach { party ->
      InputChip(
        selected = true,
        onClick = { onRemoveParty(party) },
        label = { Text("Party: $party", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium) },
        trailingIcon = {
          Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.padding(start = 4.dp))
        },
        colors = InputChipDefaults.inputChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
      )
    }

    // Month filter
    selectedMonth?.let { m ->
      InputChip(
        selected = true,
        onClick = onRemoveMonth,
        label = { Text("Month: $m", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium) },
        trailingIcon = {
          Icon(Icons.Default.Close, contentDescription = "Remove")
        },
        colors = InputChipDefaults.inputChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
      )
    }

    // Product filter
    selectedProduct?.let { p ->
      InputChip(
        selected = true,
        onClick = onRemoveProduct,
        label = { Text("Product: $p", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium) },
        trailingIcon = {
          Icon(Icons.Default.Close, contentDescription = "Remove")
        },
        colors = InputChipDefaults.inputChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
      )
    }

    // Search query filter
    if (searchQuery.isNotBlank()) {
      InputChip(
        selected = true,
        onClick = onRemoveSearch,
        label = { Text("Search: \"$searchQuery\"", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium) },
        trailingIcon = {
          Icon(Icons.Default.Close, contentDescription = "Remove")
        },
        colors = InputChipDefaults.inputChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
      )
    }
  }
}
