package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ManageEntitiesDialog(
  title: String,
  items: List<String>,
  onDismiss: () -> Unit,
  onDeleteSelected: (List<String>) -> Unit,
) {
  val selectedItems = remember { mutableStateListOf<String>() }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(28.dp),
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        if (items.isEmpty()) {
          Text(
            "No items available to manage.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          Text(
            "Select items to remove. All associated sales records will be deleted.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "${selectedItems.size} selected",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = if (selectedItems.size == items.size) "Deselect All" else "Select All",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .clickable {
                  if (selectedItems.size == items.size) {
                    selectedItems.clear()
                  } else {
                    selectedItems.clear()
                    selectedItems.addAll(items)
                  }
                }
                .padding(4.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 260.dp)
          ) {
            items(items) { item ->
              val isChecked = selectedItems.contains(item)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    if (isChecked) selectedItems.remove(item) else selectedItems.add(item)
                  }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { checked ->
                    if (checked) selectedItems.add(item) else selectedItems.remove(item)
                  }
                )
                Text(
                  text = item,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(start = 8.dp)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      if (items.isNotEmpty()) {
        Button(
          onClick = {
            if (selectedItems.isNotEmpty()) {
              onDeleteSelected(selectedItems.toList())
              onDismiss()
            }
          },
          enabled = selectedItems.isNotEmpty(),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Delete (${selectedItems.size})")
        }
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
