package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SalesEntry
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val MonthNames = listOf(
  "January", "February", "March", "April",
  "May", "June", "July", "August",
  "September", "October", "November", "December"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesEntryDialog(
  entryToEdit: SalesEntry? = null,
  knownParties: List<String>,
  knownProducts: List<String>,
  onDismiss: () -> Unit,
  onSave: (party: String, date: String, product: String, target: Double, achieved: Double, notes: String) -> Unit,
) {
  val isEditing = entryToEdit != null

  val currentCal = remember { Calendar.getInstance() }
  val currentYear = remember { currentCal.get(Calendar.YEAR) }
  val currentMonthNumber = remember { currentCal.get(Calendar.MONTH) + 1 } // 1..12
  val currentDay = remember { currentCal.get(Calendar.DAY_OF_MONTH) }
  val todayStr = remember {
    String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonthNumber, currentDay)
  }

  // Parse existing entry or initialize with defaults
  var party by remember { mutableStateOf(entryToEdit?.party ?: "") }
  var partyExpanded by remember { mutableStateOf(false) }

  var product by remember { mutableStateOf(entryToEdit?.product ?: "") }
  var productExpanded by remember { mutableStateOf(false) }

  // Target Month & Year (auto-fetched as current month & year, editable by user)
  var targetMonthIndex by remember {
    mutableIntStateOf(
      if (entryToEdit != null) (entryToEdit.monthNumber - 1).coerceIn(0, 11)
      else (currentMonthNumber - 1).coerceIn(0, 11)
    )
  }
  var targetYear by remember {
    mutableIntStateOf(entryToEdit?.year ?: currentYear)
  }
  var monthDropdownExpanded by remember { mutableStateOf(false) }
  var yearDropdownExpanded by remember { mutableStateOf(false) }

  // Achievement Date (collected specifically for Achievement Value)
  var achievementDate by remember {
    mutableStateOf(entryToEdit?.date ?: todayStr)
  }

  // Target & Achievement Values (Can be 0)
  var targetStr by remember {
    mutableStateOf(
      if (entryToEdit != null) {
        if (entryToEdit.targetValue % 1.0 == 0.0) entryToEdit.targetValue.toLong().toString()
        else entryToEdit.targetValue.toString()
      } else "0"
    )
  }

  var achievedStr by remember {
    mutableStateOf(
      if (entryToEdit != null) {
        if (entryToEdit.achievementValue % 1.0 == 0.0) entryToEdit.achievementValue.toLong().toString()
        else entryToEdit.achievementValue.toString()
      } else "0"
    )
  }

  var notes by remember { mutableStateOf(entryToEdit?.notes ?: "") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val targetNum = targetStr.toDoubleOrNull() ?: 0.0
  val achievedNum = achievedStr.toDoubleOrNull() ?: 0.0
  val calculatedPct = if (targetNum > 0) Math.round((achievedNum / targetNum) * 100).toInt() else if (achievedNum > 0) 100 else 0

  val availableYearsList = remember {
    val base = listOf(currentYear - 2, currentYear - 1, currentYear, currentYear + 1, currentYear + 2)
    (base + (entryToEdit?.year ?: currentYear)).distinct().sorted()
  }

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
            if (isEditing) "Edit Sales Record" else "Add Sales Target & Achievement",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Text(
            "Auto-fetches target month/year • Achievement can be negative",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Party Name with Autocomplete dropdown
        ExposedDropdownMenuBox(
          expanded = partyExpanded,
          onExpandedChange = { partyExpanded = !partyExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = party,
            onValueChange = {
              party = it
              partyExpanded = it.isNotEmpty()
            },
            label = { Text("Party Name *") },
            placeholder = { Text("e.g. Acme Corp") },
            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = partyExpanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("entry_party_input")
          )

          val filteredParties = knownParties.filter {
            it.contains(party, ignoreCase = true) && !it.equals(party, ignoreCase = true)
          }
          if (filteredParties.isNotEmpty()) {
            ExposedDropdownMenu(
              expanded = partyExpanded,
              onDismissRequest = { partyExpanded = false }
            ) {
              filteredParties.take(5).forEach { suggestion ->
                DropdownMenuItem(
                  text = { Text(suggestion) },
                  onClick = {
                    party = suggestion
                    partyExpanded = false
                  }
                )
              }
            }
          }
        }

        // Product Name with Autocomplete dropdown
        ExposedDropdownMenuBox(
          expanded = productExpanded,
          onExpandedChange = { productExpanded = !productExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = product,
            onValueChange = {
              product = it
              productExpanded = it.isNotEmpty()
            },
            label = { Text("Product / Service *") },
            placeholder = { Text("e.g. Cloud Hosting") },
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("entry_product_input")
          )

          val filteredProducts = knownProducts.filter {
            it.contains(product, ignoreCase = true) && !it.equals(product, ignoreCase = true)
          }
          if (filteredProducts.isNotEmpty()) {
            ExposedDropdownMenu(
              expanded = productExpanded,
              onDismissRequest = { productExpanded = false }
            ) {
              filteredProducts.take(5).forEach { suggestion ->
                DropdownMenuItem(
                  text = { Text(suggestion) },
                  onClick = {
                    product = suggestion
                    productExpanded = false
                  }
                )
              }
            }
          }
        }

        // TARGET PERIOD & VALUE SECTION
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "TARGET PERIOD & AMOUNT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  "Auto-Fetched",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Month & Year Selectors for Target
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Month Selector
              Box(modifier = Modifier.weight(1.3f)) {
                OutlinedCard(
                  onClick = { monthDropdownExpanded = true },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text(
                        "Target Month",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        MonthNames[targetMonthIndex],
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                  }
                }

                DropdownMenu(
                  expanded = monthDropdownExpanded,
                  onDismissRequest = { monthDropdownExpanded = false }
                ) {
                  MonthNames.forEachIndexed { idx, mName ->
                    DropdownMenuItem(
                      text = {
                        Text(
                          mName,
                          fontWeight = if (idx == targetMonthIndex) FontWeight.Bold else FontWeight.Normal
                        )
                      },
                      onClick = {
                        targetMonthIndex = idx
                        monthDropdownExpanded = false
                        // Sync date's month and year
                        val monthNumStr = String.format(Locale.US, "%02d", idx + 1)
                        val dayStr = try {
                          achievementDate.split("-").getOrElse(2) { "01" }
                        } catch (e: Exception) {
                          "01"
                        }
                        achievementDate = String.format(Locale.US, "%04d-%s-%s", targetYear, monthNumStr, dayStr)
                      }
                    )
                  }
                }
              }

              // Year Selector
              Box(modifier = Modifier.weight(1f)) {
                OutlinedCard(
                  onClick = { yearDropdownExpanded = true },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text(
                        "Target Year",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        targetYear.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                  }
                }

                DropdownMenu(
                  expanded = yearDropdownExpanded,
                  onDismissRequest = { yearDropdownExpanded = false }
                ) {
                  availableYearsList.forEach { yr ->
                    DropdownMenuItem(
                      text = {
                        Text(
                          yr.toString(),
                          fontWeight = if (yr == targetYear) FontWeight.Bold else FontWeight.Normal
                        )
                      },
                      onClick = {
                        targetYear = yr
                        yearDropdownExpanded = false
                        // Sync date's year
                        val monthNumStr = String.format(Locale.US, "%02d", targetMonthIndex + 1)
                        val dayStr = try {
                          achievementDate.split("-").getOrElse(2) { "01" }
                        } catch (e: Exception) {
                          "01"
                        }
                        achievementDate = String.format(Locale.US, "%04d-%s-%s", yr, monthNumStr, dayStr)
                      }
                    )
                  }
                }
              }
            }

            // Target Value Field (can be zero)
            OutlinedTextField(
              value = targetStr,
              onValueChange = { targetStr = it },
              label = { Text("Target Value (₹) (Can be 0)") },
              placeholder = { Text("0") },
              leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("entry_target_input")
            )
          }
        }

        // ACHIEVEMENT SECTION (DATE COLLECTED SPECIFICALLY FOR ACHIEVEMENT)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "ACHIEVEMENT DETAILS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = EmeraldSuccess
              )
              Text(
                "Add multiple per party/product",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Achievement Date Input
            OutlinedTextField(
              value = achievementDate,
              onValueChange = {
                achievementDate = it
                // If user enters a full date, update target month & year to match
                try {
                  val parts = it.split("-")
                  if (parts.size == 3) {
                    val y = parts[0].toIntOrNull()
                    val m = parts[1].toIntOrNull()
                    if (y != null && y in 1900..2100) targetYear = y
                    if (m != null && m in 1..12) targetMonthIndex = m - 1
                  }
                } catch (e: Exception) {
                  // ignore parsing errors while user is typing
                }
              },
              label = { Text("Achievement Date (YYYY-MM-DD) *") },
              placeholder = { Text("YYYY-MM-DD") },
              leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
              trailingIcon = {
                Surface(
                  modifier = Modifier
                    .clickable {
                      achievementDate = todayStr
                      targetYear = currentYear
                      targetMonthIndex = currentMonthNumber - 1
                    }
                    .padding(end = 8.dp),
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    "Today",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("entry_date_input")
            )

            // Achievement Value Field (can be zero or negative)
            OutlinedTextField(
              value = achievedStr,
              onValueChange = { achievedStr = it },
              label = { Text("Achievement Value (₹) (Can be negative)") },
              placeholder = { Text("0 or -500") },
              leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("entry_achieved_input")
            )
          }
        }

        // Live calculation preview card
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "Progress Preview",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  "${CurrencyFormatter.formatRupees(achievedNum)} / ${CurrencyFormatter.formatRupees(targetNum)}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  color = if (targetNum > 0 && (achievedNum / targetNum) >= 1.0) EmeraldSuccess.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    CurrencyFormatter.formatPercentage(targetNum, achievedNum),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (targetNum > 0 && (achievedNum / targetNum) >= 1.0) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val progress = if (targetNum > 0) (achievedNum / targetNum).toFloat().coerceIn(0f, 1f) else if (achievedNum > 0) 1f else 0f
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surface)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(progress)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(if (calculatedPct >= 100) EmeraldSuccess else MaterialTheme.colorScheme.primary)
              )
            }
          }
        }

        // Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes (Optional)") },
          placeholder = { Text("e.g. Q3 extension, installment 2 of 3") },
          leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
          maxLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("entry_notes_input")
        )

        if (errorMessage != null) {
          Text(
            errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (party.isBlank()) {
            errorMessage = "Please enter the party name."
            return@Button
          }
          if (product.isBlank()) {
            errorMessage = "Please enter the product or service."
            return@Button
          }
          if (achievementDate.isBlank()) {
            errorMessage = "Please enter the achievement date."
            return@Button
          }
          // Validate date format YYYY-MM-DD
          val dateParts = achievementDate.trim().split("-")
          if (dateParts.size != 3 || dateParts[0].length != 4 || dateParts[1].length != 2 || dateParts[2].length != 2) {
            errorMessage = "Date must be in YYYY-MM-DD format (e.g. 2026-08-19)."
            return@Button
          }
          if (targetNum < 0) {
            errorMessage = "Target value cannot be negative."
            return@Button
          }

          onSave(
            party.trim(),
            achievementDate.trim(),
            product.trim(),
            targetNum,
            achievedNum,
            notes.trim()
          )
          onDismiss()
        },
        modifier = Modifier.testTag("entry_save_button"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(if (isEditing) "Save Changes" else "Add Entry")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Cancel")
      }
    }
  )
}
