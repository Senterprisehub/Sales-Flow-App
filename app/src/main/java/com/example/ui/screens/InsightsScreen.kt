package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.ai.AiInsightResult
import com.example.data.model.SalesEntry
import com.example.ui.components.ChartColors
import com.example.ui.components.ProductDonutChart
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.PartyMetric
import com.example.ui.viewmodel.ProductMetric
import com.example.utils.CurrencyFormatter
import com.example.utils.PdfReportGenerator

@Composable
fun InsightsScreen(
  allEntries: List<SalesEntry>,
  topProducts: List<ProductMetric>,
  topParties: List<PartyMetric>,
  totalRevenue: Double,
  totalTarget: Double,
  allEntriesCount: Int,
  aiInsightResult: AiInsightResult?,
  isAiLoading: Boolean,
  selectedAiFocus: String,
  onRefreshAiInsights: (String) -> Unit,
  onManageParties: () -> Unit,
  onManageProducts: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var isGeneratingPdf by remember { mutableStateOf(false) }

  // Auto trigger AI insights generation if not yet populated
  LaunchedEffect(allEntriesCount) {
    if (aiInsightResult == null && !isAiLoading) {
      onRefreshAiInsights(selectedAiFocus)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header section with manage actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          "Performance Insights",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          "AI analytics & portfolio breakdown",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
          onClick = onManageParties,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Parties", style = MaterialTheme.typography.labelSmall)
        }
        OutlinedButton(
          onClick = onManageProducts,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Products", style = MaterialTheme.typography.labelSmall)
        }
      }
    }

    // ==========================================
    // GENERAL AI INSIGHT CARD (Overall Data)
    // ==========================================
    GeneralAiInsightCard(
      aiInsightResult = aiInsightResult,
      isAiLoading = isAiLoading,
      selectedFocus = selectedAiFocus,
      totalRevenue = totalRevenue,
      totalTarget = totalTarget,
      onSelectFocus = { newFocus -> onRefreshAiInsights(newFocus) },
      onRefresh = { onRefreshAiInsights(selectedAiFocus) },
      onCopySummary = { textToCopy ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Sales AI Insight", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "AI Insights copied to clipboard", Toast.LENGTH_SHORT).show()
      }
    )

    // Product Revenue Distribution Donut Chart Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(24.dp),
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
              "Product Revenue Share",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              "Distribution of total achieved sales",
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
                Icons.Default.PieChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProductDonutChart(
          topProducts = topProducts,
          totalRevenue = totalRevenue
        )
      }
    }

    // Full Product Breakdown List
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(24.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          "Product Target & Achievement",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        if (topProducts.isEmpty()) {
          Text(
            "No product data available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          topProducts.forEachIndexed { idx, prod ->
            val color = ChartColors[idx % ChartColors.size]
            val pct = prod.percentage
            val progress = if (prod.target > 0) (prod.achieved / prod.target).toFloat().coerceIn(0f, 1f) else if (prod.achieved > 0) 1f else 0f

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .background(color, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    prod.productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                Surface(
                  color = if (pct >= 100) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (pct >= 100) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                    .background(color)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                  "Achieved: ${CurrencyFormatter.formatRupees(prod.achieved)}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  "Target: ${CurrencyFormatter.formatRupees(prod.target)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Party Leaderboard Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(24.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "Top Client Leaderboard",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              "Ranked by achieved sales volume",
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
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AmberWarning,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        if (topParties.isEmpty()) {
          Text(
            "No party data available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          topParties.forEachIndexed { index, party ->
            val rank = index + 1
            val pct = party.percentage
            val progress = if (party.target > 0) (party.achieved / party.target).toFloat().coerceIn(0f, 1f) else if (party.achieved > 0) 1f else 0f

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Surface(
                    color = when (rank) {
                      1 -> AmberWarning
                      2 -> Color(0xFF94A3B8) // Silver
                      3 -> Color(0xFFB45309) // Bronze
                      else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(26.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        rank.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      party.partyName,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      "${party.entryCount} deals recorded",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  color = if (pct >= 100) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (pct >= 100) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                  "Achieved: ${CurrencyFormatter.formatRupees(party.achieved)}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = EmeraldSuccess
                )
                Text(
                  "Target: ${CurrencyFormatter.formatRupees(party.target)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // =========================================================================
    // EXPANDED DATA EXPORT & PDF DOWNLOAD CARD (Bottom of Insights Tab)
    // =========================================================================
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("download_expanded_pdf_card"),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      shape = RoundedCornerShape(24.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              "Download Expanded Report",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              "Export all records, analytics & Open-Source AI Insights in PDF",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              "PDF Report Inclusions (Complete Multi-Page Audit):",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Executive KPIs & Net Revenue Variance", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Open-Source AI Synthesis, Drivers & Actionable Recommendations", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Yearly & Quarterly Performance Attainment Breakdown", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Complete Monthly Calendar Performance Breakdown (12 Months)", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Complete Party-Wise Client Table (All Accounts)", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Complete Product-Wise Portfolio Table (All Products)", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
              Text("Complete Itemized Transaction Log (${allEntries.size} entries across all pages)", style = MaterialTheme.typography.bodySmall)
            }
          }
        }

        // Action Buttons: Download PDF and Share PDF
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              if (isGeneratingPdf) return@Button
              isGeneratingPdf = true
              try {
                val pdfFile = PdfReportGenerator.generateExpandedReportPdf(
                  context = context,
                  allEntries = allEntries,
                  topProducts = topProducts,
                  topParties = topParties,
                  totalRevenue = totalRevenue,
                  totalTarget = totalTarget,
                  aiInsightResult = aiInsightResult,
                  selectedFocus = selectedAiFocus
                )

                val uri = FileProvider.getUriForFile(
                  context,
                  "${context.packageName}.fileprovider",
                  pdfFile
                )

                val intent = Intent(Intent.ACTION_VIEW).apply {
                  setDataAndType(uri, "application/pdf")
                  addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                  addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val chooser = Intent.createChooser(intent, "Open PDF Report with...")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                Toast.makeText(context, "PDF Report generated successfully", Toast.LENGTH_SHORT).show()
              } catch (e: Exception) {
                Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
              } finally {
                isGeneratingPdf = false
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("download_pdf_report_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            if (isGeneratingPdf) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Exporting PDF...")
            } else {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "Download All Data (PDF)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
              )
            }
          }

          OutlinedButton(
            onClick = {
              if (isGeneratingPdf) return@OutlinedButton
              isGeneratingPdf = true
              try {
                val pdfFile = PdfReportGenerator.generateExpandedReportPdf(
                  context = context,
                  allEntries = allEntries,
                  topProducts = topProducts,
                  topParties = topParties,
                  totalRevenue = totalRevenue,
                  totalTarget = totalTarget,
                  aiInsightResult = aiInsightResult,
                  selectedFocus = selectedAiFocus
                )

                val uri = FileProvider.getUriForFile(
                  context,
                  "${context.packageName}.fileprovider",
                  pdfFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "application/pdf"
                  putExtra(Intent.EXTRA_STREAM, uri)
                  putExtra(Intent.EXTRA_SUBJECT, "Sales Tracker Enterprise PDF Report")
                  putExtra(Intent.EXTRA_TEXT, "Here is the complete enterprise sales report with Open-Source AI intelligence.")
                  addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Share PDF Report")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
              } catch (e: Exception) {
                Toast.makeText(context, "Share error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
              } finally {
                isGeneratingPdf = false
              }
            },
            modifier = Modifier
              .height(48.dp)
              .testTag("share_pdf_report_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share PDF", modifier = Modifier.size(18.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

/**
 * General AI Insight Component for overall commercial data with Expand / Collapse capabilities.
 */
@Composable
fun GeneralAiInsightCard(
  aiInsightResult: AiInsightResult?,
  isAiLoading: Boolean,
  selectedFocus: String,
  totalRevenue: Double,
  totalTarget: Double,
  onSelectFocus: (String) -> Unit,
  onRefresh: () -> Unit,
  onCopySummary: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isExpanded by remember { mutableStateOf(true) }

  val focusOptions = listOf(
    "General Overview",
    "Key Accounts",
    "Product Strategy",
    "Risks & Deficits",
    "Projections"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
  val shimmerAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "shimmerAlpha"
  )

  val expandRotation by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    animationSpec = tween(300),
    label = "expandRotation"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("general_ai_insight_card"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(24.dp),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // 1. AI Header Bar with Expand / Collapse trigger
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Surface(
              color = EmeraldSuccess.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                "Open-Source • Offline",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              "General AI Insights",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              if (isExpanded) "On-device open-source model • 100% offline" else "Tap to expand AI analysis breakdown",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Action Buttons: Copy, Refresh, and Expand/Collapse Arrow
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (aiInsightResult != null && !isAiLoading) {
            IconButton(
              onClick = {
                val fullText = buildString {
                  append("=== OPEN-SOURCE SALES AI INSIGHTS (OFFLINE) ===\n")
                  append("Engine: ${aiInsightResult.sourceModel}\n")
                  append("Focus: $selectedFocus\n\n")
                  append("${aiInsightResult.summary}\n\n")
                  append("KEY HIGHLIGHTS:\n")
                  aiInsightResult.keyHighlights.forEach { append("• $it\n") }
                  append("\nRISKS & OPPORTUNITIES:\n")
                  aiInsightResult.opportunitiesAndRisks.forEach { append("• $it\n") }
                  append("\nRECOMMENDATIONS:\n")
                  aiInsightResult.strategicRecommendations.forEach { append("• $it\n") }
                }
                onCopySummary(fullText)
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                Icons.Default.ContentCopy,
                contentDescription = "Copy AI Summary",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          IconButton(
            onClick = onRefresh,
            enabled = !isAiLoading,
            modifier = Modifier.size(36.dp)
          ) {
            if (isAiLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
              )
            } else {
              Icon(
                Icons.Default.Refresh,
                contentDescription = "Regenerate AI Insights",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
              .size(36.dp)
              .testTag("ai_insight_expand_collapse_button")
          ) {
            Icon(
              Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "Collapse AI Insight" else "Expand AI Insight",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(22.dp)
                .rotate(expandRotation)
            )
          }
        }
      }

      // Collapsible Body Section
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column {
          Spacer(modifier = Modifier.height(14.dp))

          // 2. Analytical Focus Filter Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            focusOptions.forEach { focus ->
              val isSelected = selectedFocus.equals(focus, ignoreCase = true)
              FilterChip(
                selected = isSelected,
                onClick = { onSelectFocus(focus) },
                label = {
                  Text(
                    focus,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3. AI Content View (Loading vs Populated)
          if (isAiLoading) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  strokeWidth = 2.5.dp,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  "Synthesizing overall commercial intelligence...",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              // Shimmer placeholders
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(60.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha))
              )
              Box(
                modifier = Modifier
                  .fillMaxWidth(0.8f)
                  .height(30.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha))
              )
            }
          } else if (aiInsightResult != null) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              // Executive Summary Banner
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AmberWarning,
                    modifier = Modifier
                      .size(22.dp)
                      .padding(top = 2.dp)
                  )
                  Text(
                    aiInsightResult.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                  )
                }
              }

              // Section A: Key Strategic Highlights
              if (aiInsightResult.keyHighlights.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    "KEY PERFORMANCE HIGHLIGHTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.primary
                  )
                  aiInsightResult.keyHighlights.forEach { highlight ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.Top
                    ) {
                      Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier
                          .size(16.dp)
                          .padding(top = 3.dp)
                      )
                      Text(
                        highlight,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }
              }

              // Section B: Risks & Opportunities
              if (aiInsightResult.opportunitiesAndRisks.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    "OPPORTUNITIES & EXPOSURE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = AmberWarning
                  )
                  aiInsightResult.opportunitiesAndRisks.forEach { item ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.Top
                    ) {
                      Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier
                          .size(16.dp)
                          .padding(top = 3.dp)
                      )
                      Text(
                        item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }
              }

              // Section C: Strategic Recommendations
              if (aiInsightResult.strategicRecommendations.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    "ACTIONABLE RECOMMENDATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = EmeraldSuccess
                  )
                  aiInsightResult.strategicRecommendations.forEach { action ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.Top
                    ) {
                      Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier
                          .size(16.dp)
                          .padding(top = 3.dp)
                      )
                      Text(
                        action,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
