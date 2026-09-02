package com.example.donemate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donemate.data.model.Progress
import com.example.donemate.data.model.Task
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TaskItem(
    task: Task,
    onTaskProgressChange: (Task, Int) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    onNavigateToEdit: (id: String) -> Unit,
    onArchiveTaskClick: (Task) -> Unit,
    isTaskArchived: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val menuItemData: Map<String, Int> = Progress.entries.associate { it.label to it.value }

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onNavigateToEdit(task.id) }) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(4.5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                task.imageUri?.let { imageUri ->
                    TaskImage(imageUri)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.width(5.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, fontSize = 28.sp, modifier = Modifier.width(213.dp))

                        Box(
                            modifier = Modifier
                                .clickable { expanded = true }
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Progress.labelFromInt(task.progress),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                            ) {
                                menuItemData.forEach { (label, value) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            onTaskProgressChange(task, value)
                                            expanded = false
                                        }
                                    )
                                }
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    task.description?.let {
                        Text(
                            if (task.description.length > 90) {
                                task.description.take(90) + "..."
                            } else {
                                task.description
                            }, fontSize = 15.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            if (task.reminderDateTime != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Reminder at " + task.reminderDateTime.atZone(ZoneId.systemDefault())
                                            .let {
                                                it.format(
                                                    DateTimeFormatter.ofPattern(
                                                        when {
                                                            it.year == LocalDate.now().year -> "dd MMM HH:mm"
                                                            else -> "dd MMM yyyy HH:mm"
                                                        }
                                                    )
                                                )
                                            })
                                }
                            }
                            if (task.dueDateTime != null) {
                                Spacer(modifier = Modifier.height(10.dp))

                                Row {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Due " + task.dueDateTime.atZone(ZoneId.systemDefault())
                                            .let {
                                                it.format(
                                                    DateTimeFormatter.ofPattern(
                                                        when {
                                                            it.year == LocalDate.now().year -> "dd MMM HH:mm"
                                                            else -> "dd MMM yyyy HH:mm"
                                                        }
                                                    )
                                                )
                                            })
                                }
                            }

                        }
                        Column {
                            Row {
                                IconButton(onClick = { onArchiveTaskClick(task) }) {
                                    Icon(
                                        imageVector = if (isTaskArchived) Icons.Outlined.Unarchive else Icons.Outlined.Archive,
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                                IconButton(onClick = { onDeleteTaskClick(task) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }

                    }

                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}

