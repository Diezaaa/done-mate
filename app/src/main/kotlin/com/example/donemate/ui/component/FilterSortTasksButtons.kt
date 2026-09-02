package com.example.donemate.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.donemate.data.model.Progress
import com.example.donemate.ui.common.SortingType
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun FilterSortTasksButtons(
    expandedSort: Boolean,
    sortingType: SortingType,
    onSortingTypeChange: (SortingType) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onProgressFilterChange: (Int) -> Unit,
    progressFilter: Int
) {
    val menuItemData: Map<String, Int> =
        Progress.entries.associate { it.label to it.value }
    Row {
        OutlinedIconButton(
            onClick = { onExpandedChange(true) }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Sort",
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = expandedSort,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            Text("Sorting by", modifier = Modifier.padding(start = 3.dp))

            DropdownMenuItem(
                enabled = sortingType != SortingType.TimeUpdated,
                text = { Text("Time updated") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null
                    )
                },
                onClick = {
                    onSortingTypeChange(SortingType.TimeUpdated)
                }
            )

            DropdownMenuItem(
                enabled = sortingType != SortingType.DueDateTime,
                text = { Text("Due time") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                },
                onClick = {
                    onSortingTypeChange(SortingType.DueDateTime)
                }
            )


            DropdownMenuItem(
                enabled = sortingType != SortingType.ReminderDateTime,
                text = { Text("Reminder time") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null
                    )
                },
                onClick = {
                    onSortingTypeChange(SortingType.ReminderDateTime)
                }
            )
        }
        Spacer(modifier = Modifier.width(5.dp))

        SingleChoiceSegmentedButtonRow {
            menuItemData.forEach { (label, phase) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = phase,
                        count = menuItemData.size
                    ),
                    onClick = {
                        if (progressFilter == phase) {
                            onProgressFilterChange(-1)

                        } else {
                            onProgressFilterChange(phase)
                        }
                    },
                    selected = phase == progressFilter,
                    label = { Text(label) }
                )
            }

        }
    }
}