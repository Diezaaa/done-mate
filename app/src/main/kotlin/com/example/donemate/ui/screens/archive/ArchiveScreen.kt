package com.example.donemate.ui.screens.archive

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import com.example.donemate.ui.common.SortingType
import com.example.donemate.ui.common.TasksUiState
import com.example.donemate.ui.component.CustomLoadingIndicator
import com.example.donemate.ui.component.FilterSortTasksButtons
import com.example.donemate.ui.component.TaskItem
import com.example.donemate.ui.component.TasksDockedSearchBar
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ArchiveScreen(
    vm: ArchiveViewModel,
    onNavigateToEdit: (id: String) -> Unit,
    isOfflineDialogShownOnce: Boolean,
    onOfflineDialogDismiss: () -> Unit,
    onNavigateToTasks: () -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    ArchiveScreen(
        uiState = uiState,
        onTaskProgressChange = vm::updateTaskProgress,
        onDeleteTaskClick = vm::deleteTask,
        onNavigateToEdit = onNavigateToEdit,
        onQueryChange = vm::updateQuery,
        onProgressFilterChange = vm::updateProgressFilter,
        onSortingTypeChange = vm::updateSortingType,
        isOfflineDialogShownOnce = isOfflineDialogShownOnce,
        onOfflineDialogDismiss = onOfflineDialogDismiss,
        onSync = vm::sync,
        onNavigateToTasks = onNavigateToTasks,
        onUnArchiveTaskClick = vm::unArchiveTask
    )
}

@Composable
fun ArchiveScreen(
    uiState: TasksUiState,
    onTaskProgressChange: (Task, Int) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onProgressFilterChange: (Int) -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    isOfflineDialogShownOnce: Boolean,
    onOfflineDialogDismiss: () -> Unit,
    onSync: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onUnArchiveTaskClick: (Task) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateToTasks() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(7.dp))
                Text("Archived tasks", fontSize = 22.sp)
            }
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
                        TasksDockedSearchBar(
                            visibleTasks = uiState.visibleTasks,
                            onTaskProgressChange = onTaskProgressChange,
                            onDeleteTaskClick = onDeleteTaskClick,
                            onNavigateToEdit = onNavigateToEdit,
                            onQueryChange = onQueryChange,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            query = uiState.query,
                            searchBarModifier = Modifier
                                .weight(1f),
                            onArchiveTaskClick = onUnArchiveTaskClick,
                            areTasksArchived = true
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    if (!expanded) {
                        Row {
                            var expandedSort by rememberSaveable { mutableStateOf(false) }
                            FilterSortTasksButtons(
                                expandedSort = expandedSort,
                                sortingType = uiState.sortingType,
                                onSortingTypeChange = onSortingTypeChange,
                                onExpandedChange = { expandedSort = it },
                                onProgressFilterChange = onProgressFilterChange,
                                progressFilter = uiState.progressFilter
                            )
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
                                        onNavigateToEdit = onNavigateToEdit,
                                        onArchiveTaskClick = onUnArchiveTaskClick,
                                        isTaskArchived = true
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