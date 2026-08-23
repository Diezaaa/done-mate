package com.example.donemate.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.donemate.data.model.Progress
import com.example.donemate.data.model.Task
import kotlinx.coroutines.delay
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.Alignment
import com.example.donemate.data.common.Result
import com.example.donemate.ui.component.CustomLoadingIndicator
import com.example.donemate.ui.component.TaskImage
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun TasksScreen(
    vm: TasksViewModel,
    navigateToEdit: (id: String) -> Unit,
    navigateToAdd: () -> Unit,
    isOfflineDialogShownOnce: Boolean,
    onOfflineDialogDismiss: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    TasksList(
        uiState = uiState,
        onTaskProgressChange = vm::updateTaskProgress,
        onDeleteTaskClick = vm::deleteTask,
        navigateToEdit = navigateToEdit,
        navigateToAdd = navigateToAdd,
        onQueryChange = vm::updateQuery,
        onProgressFilterChange = vm::updateProgressFilter,
        onSortingTypeChange = vm::updateSortingType,
        isOfflineDialogShownOnce = isOfflineDialogShownOnce,
        onOfflineDialogDismiss = onOfflineDialogDismiss,
        onSync = vm::sync
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TasksList(
    uiState: TasksUiState,
    onTaskProgressChange: (Task, Int) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    navigateToEdit: (String) -> Unit,
    navigateToAdd: () -> Unit,
    onQueryChange: (String) -> Unit,
    onProgressFilterChange: (Int) -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    isOfflineDialogShownOnce: Boolean,
    onOfflineDialogDismiss: () -> Unit,
    onSync: () -> Unit
) {
    LaunchedEffect(Unit) {
        while (true) {
            onSync()
            delay(5_000.milliseconds)
        }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is TasksUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomLoadingIndicator()
                    }
                }

                is TasksUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                            Text(text = uiState.message, modifier = Modifier.width(250.dp))
                        }
                    }
                }

                is TasksUiState.Success -> {
                    if (!isOfflineDialogShownOnce && uiState.syncingStatus is Result.Error) {
                        AlertDialog(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SignalWifiConnectedNoInternet4,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                            },
                            title = {
                                Text(text = "You're offline")
                            },
                            text = {
                                Text(text = "Your tasks aren't synced with your other devices while you're offline!")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onOfflineDialogDismiss()
                                    }
                                ) {
                                    Text("Ok")
                                }
                            },
                            onDismissRequest = {}
                        )
                    }


                    Row(modifier = Modifier.fillMaxWidth()) {
                        DockedSearchBar(
                            modifier = Modifier.weight(1f),
                            expanded = expanded,
                            inputField = {
                                SearchBarDefaults.InputField(
                                    query = uiState.query,
                                    onQueryChange = {
                                        onQueryChange(it)
                                        expanded = true
                                    },
                                    onSearch = {},
                                    expanded = expanded,
                                    onExpandedChange = {},
                                    placeholder = { Text("Search by title or description") },
                                    leadingIcon = {
                                        if (!expanded) {
                                            Icon(Icons.Default.Search, "Search")
                                        } else {
                                            IconButton(
                                                onClick = {
                                                    expanded = false
                                                    onQueryChange("")
                                                }
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                            }
                                        }
                                    }
                                )
                            },
                            onExpandedChange = { isExpanded ->
                                expanded = isExpanded

                                if (!isExpanded) {
                                    onQueryChange("")
                                }
                            }
                        ) {
                            if (uiState.query.isNotBlank()) {
                                LazyColumn {
                                    item {
                                        Box(modifier = Modifier.height(15.dp)) {}
                                    }

                                    items(uiState.visibleTasks, key = { it.id }) { task ->
                                        Box(
                                            modifier = Modifier.padding(9.dp, 9.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TaskItem(
                                                task,
                                                onTaskProgressChange = onTaskProgressChange,
                                                onDeleteTaskClick = onDeleteTaskClick,
                                                navigateToEdit = navigateToEdit
                                            )
                                        }
                                    }

                                    item {
                                        Box(modifier = Modifier.height(15.dp)) {}
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    val menuItemData: Map<String, Int> =
                        Progress.entries.associate { it.label to it.value }

                    if (!expanded) {
                        Row {
                            Box {
                                var expandedSort by rememberSaveable { mutableStateOf(false) }

                                OutlinedIconButton(
                                    onClick = { expandedSort = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = "Sort",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = expandedSort,
                                    onDismissRequest = { expandedSort = false }
                                ) {
                                    Text("Sorting by", modifier = Modifier.padding(start = 3.dp))

                                    DropdownMenuItem(
                                        enabled = uiState.sortingType != SortingType.TimeUpdated,
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
                                        enabled = uiState.sortingType != SortingType.DueDateTime,
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
                                        enabled = uiState.sortingType != SortingType.ReminderDateTime,
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
                                            if (uiState.progressFilter == phase) {
                                                onProgressFilterChange(-1)

                                            }
                                            else {
                                                onProgressFilterChange(phase)
                                            }
                                        },
                                        selected = phase == uiState.progressFilter,
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.5.dp))

                    if (!expanded) {
                        Row(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(
                                    bottom = 96.dp
                                )
                            ) {
                                items(uiState.visibleTasks, key = { it.id }) { task ->
                                    TaskItem(
                                        task = task,
                                        onTaskProgressChange = onTaskProgressChange,
                                        onDeleteTaskClick = onDeleteTaskClick,
                                        navigateToEdit = navigateToEdit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navigateToAdd() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskProgressChange: (Task, Int) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    navigateToEdit: (id: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val menuItemData: Map<String, Int> = Progress.entries.associate { it.label to it.value }

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { navigateToEdit(task.id) }) {
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
                            IconButton(onClick = { onDeleteTaskClick(task) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                        }

                    }

                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}

