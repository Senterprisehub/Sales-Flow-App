package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.MonthlyMetric
import com.example.ui.viewmodel.ProductMetric
import com.example.ui.viewmodel.QuarterMetric
import com.example.utils.CurrencyFormatter

val ChartColors = listOf(
  Color(0xFF0061A4), // Deep Cobalt
  Color(0xFF3B6939), // Forest Green
  Color(0xFF6F5778), // Plum Purple
  Color(0xFFD97706), // Amber
  Color(0xFF0284C7), // Sky Blue
  Color(0xFF7C3AED), // Violet
  Color(0xFFD946EF), // Fuchsia
  Color(0xFF059669), // Teal
)

@Composable
fun MonthlySalesBarChart(
  monthlyMetrics: List<MonthlyMetric>,
  modifier: Modifier = Modifier,
) {
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(monthlyMetrics) {
    animProgress.snapTo(0f)
    animProgress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
  }

  val maxVal = remember(monthlyMetrics) {
    val peak = monthlyMetrics.maxOfOrNull { maxOf(it.target, it.achieved) } ?: 100000.0
    if (peak <= 0.0) 100000.0 else peak * 1.15
  }

  val primaryColor = MaterialTheme.colorScheme.primary
  val successColor = EmeraldSuccess
  val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
  val textColor = MaterialTheme.colorScheme.onSurfaceVariant

  Column(modifier = modifier.fillMaxWidth()) {
    // Legend & Active Tooltip Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(primaryColor, RoundedCornerShape(2.dp))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Target", style = MaterialTheme.typography.labelMedium, color = textColor)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(successColor, RoundedCornerShape(2.dp))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Achieved", style = MaterialTheme.typography.labelMedium, color = textColor)
        }
      }

      Text(
        text = if (selectedIndex != null) "Tap bar to inspect" else "Tap any month",
        style = MaterialTheme.typography.labelSmall,
        color = textColor
      )
    }

    // Selected month tooltip card
    selectedIndex?.let { idx ->
      val metric = monthlyMetrics.getOrNull(idx)
      if (metric != null) {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                metric.monthName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
              )
              Text(
                "${metric.entryCount} entries",
                style = MaterialTheme.typography.labelSmall,
                color = textColor
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              Column(horizontalAlignment = Alignment.End) {
                Text("Target", style = MaterialTheme.typography.labelSmall, color = primaryColor)
                Text(
                  CurrencyFormatter.formatCompact(metric.target),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodySmall
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Achieved", style = MaterialTheme.typography.labelSmall, color = successColor)
                Text(
                  CurrencyFormatter.formatCompact(metric.achieved),
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.bodySmall,
                  color = successColor
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Rate", style = MaterialTheme.typography.labelSmall, color = textColor)
                Surface(
                  color = if (metric.percentage >= 100) EmeraldSuccess.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    CurrencyFormatter.formatPercentage(metric.target, metric.achieved),
                    fontWeight = FontWeight.Bold,
                    color = if (metric.percentage >= 100) EmeraldSuccess else AmberWarning,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Canvas Chart
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(monthlyMetrics) {
            detectTapGestures { offset ->
              val totalWidth = size.width
              val leftPadding = 40.dp.toPx()
              val usableWidth = totalWidth - leftPadding
              val slotWidth = usableWidth / monthlyMetrics.size.coerceAtLeast(1)
              if (offset.x >= leftPadding) {
                val index = ((offset.x - leftPadding) / slotWidth).toInt()
                if (index in monthlyMetrics.indices) {
                  selectedIndex = if (selectedIndex == index) null else index
                }
              }
            }
          }
      ) {
        val width = size.width
        val height = size.height
        val leftPadding = 40.dp.toPx()
        val bottomPadding = 24.dp.toPx()
        val topPadding = 12.dp.toPx()
        val chartHeight = height - bottomPadding - topPadding
        val chartWidth = width - leftPadding

        // Draw 4 Horizontal Grid lines & scale labels
        val gridCount = 4
        for (i in 0..gridCount) {
          val y = topPadding + chartHeight * (i.toFloat() / gridCount)
          drawLine(
            color = outlineColor,
            start = Offset(leftPadding, y),
            end = Offset(width, y),
            strokeWidth = 1f
          )

          val labelVal = maxVal * (1f - (i.toFloat() / gridCount))
          val labelText = CurrencyFormatter.formatCompact(labelVal)
          drawContext.canvas.nativeCanvas.drawText(
            labelText,
            4.dp.toPx(),
            y + 4.dp.toPx(),
            android.graphics.Paint().apply {
              color = android.graphics.Color.GRAY
              textSize = 24f
              isAntiAlias = true
            }
          )
        }

        // Draw Bars for each month
        val count = monthlyMetrics.size.coerceAtLeast(1)
        val slotWidth = chartWidth / count
        val barGroupWidth = slotWidth * 0.72f
        val individualBarWidth = barGroupWidth / 2.2f

        monthlyMetrics.forEachIndexed { i, metric ->
          val slotX = leftPadding + (i * slotWidth)
          val groupStartX = slotX + (slotWidth - barGroupWidth) / 2f
          val isSelected = selectedIndex == i

          // Highlight column background if selected
          if (isSelected) {
            drawRoundRect(
              color = primaryColor.copy(alpha = 0.1f),
              topLeft = Offset(slotX + 2.dp.toPx(), topPadding),
              size = Size(slotWidth - 4.dp.toPx(), chartHeight),
              cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
          }

          val targetHeight = ((metric.target / maxVal) * chartHeight * animProgress.value).toFloat().coerceAtLeast(0f)
          val achievedHeight = ((metric.achieved / maxVal) * chartHeight * animProgress.value).toFloat().coerceAtLeast(0f)

          val targetTop = topPadding + chartHeight - targetHeight
          val achievedTop = topPadding + chartHeight - achievedHeight

          // Target Bar (Indigo)
          if (targetHeight > 0f) {
            drawRoundRect(
              color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.8f),
              topLeft = Offset(groupStartX, targetTop),
              size = Size(individualBarWidth, targetHeight),
              cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
          }

          // Achieved Bar (Emerald)
          if (achievedHeight > 0f) {
            drawRoundRect(
              color = if (isSelected) successColor else successColor.copy(alpha = 0.9f),
              topLeft = Offset(groupStartX + individualBarWidth + 2.dp.toPx(), achievedTop),
              size = Size(individualBarWidth, achievedHeight),
              cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
          }

          // Month short label on X axis
          drawContext.canvas.nativeCanvas.drawText(
            metric.monthShort,
            slotX + slotWidth / 2f,
            height - 6.dp.toPx(),
            android.graphics.Paint().apply {
              color = if (isSelected) android.graphics.Color.DKGRAY else android.graphics.Color.GRAY
              textSize = 24f
              textAlign = android.graphics.Paint.Align.CENTER
              isFakeBoldText = isSelected
              isAntiAlias = true
            }
          )
        }
      }
    }
  }
}

@Composable
fun ProductDonutChart(
  topProducts: List<ProductMetric>,
  totalRevenue: Double,
  modifier: Modifier = Modifier,
) {
  val animProgress = remember { Animatable(0f) }

  LaunchedEffect(topProducts) {
    animProgress.snapTo(0f)
    animProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
  }

  if (topProducts.isEmpty() || totalRevenue <= 0.0) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .height(180.dp),
      contentAlignment = Alignment.Center
    ) {
      Text("No product data to display", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    return
  }

  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Donut Canvas
    Box(
      modifier = Modifier
        .size(150.dp)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        var startAngle = -90f
        topProducts.forEachIndexed { index, item ->
          val sweepFraction = (item.achieved / totalRevenue).toFloat()
          val sweepAngle = sweepFraction * 360f * animProgress.value
          val color = ChartColors[index % ChartColors.size]

          drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
          )
          startAngle += sweepAngle
        }
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
          CurrencyFormatter.formatCompact(totalRevenue),
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )
      }
    }

    Spacer(modifier = Modifier.width(16.dp))

    // Product breakdown list
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      topProducts.take(5).forEachIndexed { index, product ->
        val pctStr = CurrencyFormatter.formatPercentage(totalRevenue, product.achieved)
        val color = ChartColors[index % ChartColors.size]

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              product.productName,
              style = MaterialTheme.typography.bodySmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              CurrencyFormatter.formatCompact(product.achieved),
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "($pctStr)",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun QuarterlyPerformanceGrid(
  quarterlyMetrics: List<QuarterMetric>,
  modifier: Modifier = Modifier,
  selectedQuarter: String? = null,
  onSelectQuarter: ((String) -> Unit)? = null,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      quarterlyMetrics.take(2).forEach { q ->
        QuarterCard(
          quarterMetric = q,
          isSelected = selectedQuarter == q.quarter,
          onClick = onSelectQuarter?.let { cb -> { cb(q.quarter) } },
          modifier = Modifier.weight(1f)
        )
      }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      quarterlyMetrics.drop(2).take(2).forEach { q ->
        QuarterCard(
          quarterMetric = q,
          isSelected = selectedQuarter == q.quarter,
          onClick = onSelectQuarter?.let { cb -> { cb(q.quarter) } },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun QuarterCard(
  quarterMetric: QuarterMetric,
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  val pct = quarterMetric.percentage
  val isPositive = quarterMetric.achieved >= quarterMetric.target

  Card(
    modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(20.dp),
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            quarterMetric.quarter,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
          )
          if (isSelected) {
            Surface(
              color = MaterialTheme.colorScheme.primary,
              shape = CircleShape,
              modifier = Modifier.size(6.dp)
            ) {}
          }
        }
        Surface(
          color = if (pct >= 100) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            CurrencyFormatter.formatPercentage(quarterMetric.target, quarterMetric.achieved),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (pct >= 100) EmeraldSuccess else AmberWarning,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Progress bar
      val progress = if (quarterMetric.target > 0) (quarterMetric.achieved / quarterMetric.target).toFloat().coerceIn(0f, 1f) else 0f
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

      Spacer(modifier = Modifier.height(10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text("Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(CurrencyFormatter.formatCompact(quarterMetric.target), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Achieved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            CurrencyFormatter.formatCompact(quarterMetric.achieved),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  valueColor: Color = MaterialTheme.colorScheme.onSurface,
  badgeText: String? = null,
  badgeColor: Color = EmeraldSuccess,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
    shape = RoundedCornerShape(20.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          title.uppercase(),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 0.5.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (badgeText != null) {
          Surface(
            color = badgeColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              badgeText,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = badgeColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        value,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = valueColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
