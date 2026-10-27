package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SalesEntry
import com.example.ui.components.ManageEntitiesDialog
import com.example.ui.components.SalesEntryDialog
import com.example.ui.components.UserManualDialog
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SalesViewModel) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // States from ViewModel
  val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
  val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
  val activeParties by viewModel.activePartyFilters.collectAsStateWithLifecycle()
  val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
  val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
  val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()
  val knownParties by viewModel.knownParties.collectAsStateWithLifecycle()
  val knownProducts by viewModel.knownProducts.collectAsStateWithLifecycle()
  val yearlyPartyFilter by viewModel.yearlyPartyFilter.collectAsStateWithLifecycle()
  val yearlyProductFilter by viewModel.yearlyProductFilter.collectAsStateWithLifecycle()
  val yearlySummary by viewModel.yearlySummary.collectAsStateWithLifecycle()
  val selectedQuarter by viewModel.selectedQuarter.collectAsStateWithLifecycle()
  val quarterlyPartyFilter by viewModel.quarterlyPartyFilter.collectAsStateWithLifecycle()
  val quarterlyProductFilter by viewModel.quarterlyProductFilter.collectAsStateWithLifecycle()
  val quarterlyDetailReport by viewModel.quarterlyDetailReport.collectAsStateWithLifecycle()
  val aiInsightResult by viewModel.aiInsightResult.collectAsStateWithLifecycle()
  val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
  val selectedAiFocus by viewModel.selectedAiFocus.collectAsStateWithLifecycle()
  val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

  // Navigation tab index: 0 = Entries, 1 = Yearly Analysis, 2 = Insights
  var selectedTab by remember { mutableIntStateOf(0) }

  // Dialog states
  var showAddDialog by remember { mutableStateOf(false) }
  var entryToEdit by remember { mutableStateOf<SalesEntry?>(null) }
  var showManualDialog by remember { mutableStateOf(false) }
  var showResetDialog by remember { mutableStateOf(false) }
  var manageType by remember { mutableStateOf<String?>(null) } // "party" or "product"
  var menuExpanded by remember { mutableStateOf(false) }

  // CSV Import launcher
  val csvPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let { viewModel.importCsv(context, it) }
  }

  // Handle messages
  LaunchedEffect(userMessage) {
    userMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearUserMessage()
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              "Sales Flow",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              "DEVELOPED BY S.R SOFTWARE STUDIO",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.8.sp,
              color = MaterialTheme.colorScheme.primary
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { showManualDialog = true },
            modifier = Modifier.testTag("topbar_manual_btn")
          ) {
            Icon(Icons.Default.HelpOutline, contentDescription = "Manual", tint = MaterialTheme.colorScheme.primary)
          }
        },
        actions = {
          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier.testTag("topbar_menu_btn")
            ) {
              Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Import CSV") },
                leadingIcon = { Icon(Icons.Default.Upload, contentDescription = null) },
                onClick = {
                  menuExpanded = false
                  csvPickerLauncher.launch("text/*")
                }
              )
              DropdownMenuItem(
                text = { Text("Download / Share CSV") },
                leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                onClick = {
                  menuExpanded = false
                  val file = viewModel.exportCsvToCache(context)
                  if (file != null) {
                    val fileUri = FileProvider.getUriForFile(
                      context,
                      "${context.packageName}.fileprovider",
                      file
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "text/csv"
                      putExtra(Intent.EXTRA_STREAM, fileUri)
                      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Sales Data CSV"))
                  }
                }
              )
              DropdownMenuItem(
                text = { Text("Manage Parties") },
                onClick = {
                  menuExpanded = false
                  manageType = "party"
                }
              )
              DropdownMenuItem(
                text = { Text("Manage Products") },
                onClick = {
                  menuExpanded = false
                  manageType = "product"
                }
              )
              DropdownMenuItem(
                text = { Text("Reset All Data", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                  menuExpanded = false
                  showResetDialog = true
                }
              )
            }
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = {
            Icon(
              if (selectedTab == 0) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
              contentDescription = "Entries"
            )
          },
          label = { Text("Entries") },
          modifier = Modifier.testTag("nav_tab_entries")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = {
            Icon(
              if (selectedTab == 1) Icons.Filled.Analytics else Icons.Outlined.Analytics,
              contentDescription = "Yearly Analysis"
            )
          },
          label = { Text("Yearly Analysis") },
          modifier = Modifier.testTag("nav_tab_yearly")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = {
            Icon(
              if (selectedTab == 2) Icons.Filled.PieChart else Icons.Outlined.PieChart,
              contentDescription = "Insights"
            )
          },
          label = { Text("Insights") },
          modifier = Modifier.testTag("nav_tab_insights")
        )
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("main_add_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Sales Record")
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> {
          val totTarget = filteredEntries.sumOf { it.targetValue }
          val totAchieved = filteredEntries.sumOf { it.achievementValue }
          val pct = if (totTarget > 0) Math.round((totAchieved / totTarget) * 100).toInt() else 0

          EntriesScreen(
            entries = filteredEntries,
            totalTarget = totTarget,
            totalAchieved = totAchieved,
            achievementPct = pct,
            totalEntriesCount = filteredEntries.size,
            activeParties = activeParties,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            selectedProduct = selectedProduct,
            searchQuery = searchQuery,
            availableYears = availableYears,
            knownProducts = knownProducts,
            knownParties = knownParties,
            onSearchChange = viewModel::setSearchQuery,
            onAddPartyFilter = viewModel::addPartyFilter,
            onRemovePartyFilter = viewModel::removePartyFilter,
            onSelectMonth = viewModel::setMonthFilter,
            onSelectYear = viewModel::setYearFilter,
            onSelectProduct = viewModel::setProductFilter,
            onClearFilters = viewModel::clearFilters,
            onEditEntry = { entryToEdit = it },
            onDeleteEntry = viewModel::deleteEntry,
            onAddNewEntry = { showAddDialog = true }
          )
        }
        1 -> {
          YearlyAnalysisDashboardScreen(
            summary = yearlySummary,
            quarterlyReport = quarterlyDetailReport,
            availableYears = availableYears,
            selectedYear = selectedYear,
            knownParties = knownParties,
            selectedParty = yearlyPartyFilter,
            knownProducts = knownProducts,
            selectedProduct = yearlyProductFilter,
            selectedQuarter = selectedQuarter,
            selectedQuarterlyParty = quarterlyPartyFilter,
            selectedQuarterlyProduct = quarterlyProductFilter,
            onSelectYear = viewModel::setYearFilter,
            onSelectParty = viewModel::setYearlyPartyFilter,
            onSelectProduct = viewModel::setYearlyProductFilter,
            onClearFilters = viewModel::clearYearlyFilters,
            onSelectQuarter = viewModel::setSelectedQuarter,
            onSelectQuarterlyParty = viewModel::setQuarterlyPartyFilter,
            onSelectQuarterlyProduct = viewModel::setQuarterlyProductFilter,
            onClearQuarterlyFilters = viewModel::clearQuarterlyFilters
          )
        }
        2 -> {
          InsightsScreen(
            allEntries = allEntries,
            topProducts = yearlySummary.topProducts,
            topParties = yearlySummary.topParties,
            totalRevenue = yearlySummary.totalAchieved,
            totalTarget = yearlySummary.totalTarget,
            allEntriesCount = allEntries.size,
            aiInsightResult = aiInsightResult,
            isAiLoading = isAiLoading,
            selectedAiFocus = selectedAiFocus,
            onRefreshAiInsights = viewModel::refreshAiInsights,
            onManageParties = { manageType = "party" },
            onManageProducts = { manageType = "product" }
          )
        }
      }
    }
  }

  // Add / Edit Entry Dialog
  if (showAddDialog || entryToEdit != null) {
    SalesEntryDialog(
      entryToEdit = entryToEdit,
      knownParties = knownParties,
      knownProducts = knownProducts,
      onDismiss = {
        showAddDialog = false
        entryToEdit = null
      },
      onSave = { party, date, product, target, achieved, notes ->
        if (entryToEdit != null) {
          viewModel.updateEntry(
            entryToEdit!!.copy(
              party = party,
              date = date,
              product = product,
              targetValue = target,
              achievementValue = achieved,
              notes = notes
            )
          )
        } else {
          viewModel.addEntry(party, date, product, target, achieved, notes)
        }
        showAddDialog = false
        entryToEdit = null
      }
    )
  }

  // User Manual Dialog
  if (showManualDialog) {
    UserManualDialog(onDismiss = { showManualDialog = false })
  }

  // Manage Parties / Products Dialog
  manageType?.let { type ->
    val items = if (type == "party") knownParties else knownProducts
    ManageEntitiesDialog(
      title = if (type == "party") "Delete Parties" else "Delete Products",
      items = items,
      onDismiss = { manageType = null },
      onDeleteSelected = { selected ->
        if (type == "party") {
          viewModel.deleteParties(selected)
        } else {
          viewModel.deleteProducts(selected)
        }
        manageType = null
      }
    )
  }

  // Reset All Data Confirmation Dialog
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      shape = RoundedCornerShape(16.dp),
      title = { Text("Reset All Data", fontWeight = FontWeight.Bold) },
      text = {
        Text("Are you sure you want to delete all saved sales records and reset the app? This action cannot be undone.")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetAllData()
            showResetDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Reset Everything")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showResetDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
