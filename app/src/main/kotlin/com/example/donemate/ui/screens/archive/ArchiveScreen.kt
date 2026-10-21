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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.data.model.Task
import com.example.donemate.ui.common.SortingType
import com.example.donemate.ui.common.TasksUiState
import com.example.donemate.ui.component.CustomLoadingIndicator
import com.example.donemate.ui.component.ErrorMessage
import com.example.donemate.ui.component.FilterSortTasksButtons
import com.example.donemate.ui.component.OfflineAlertDialog
import com.example.donemate.ui.component.TaskItem
import com.example.donemate.ui.component.TasksDockedSearchBar
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel,
    onNavigateToEdit: (id: String) -> Unit,
    onNavigateToTasks: () -> Unit,
    wasOfflineDialogShown: Boolean,
    onOfflineDialogDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    ArchiveScreen(
        uiState = uiState,
        onTaskProgressChange = viewModel::updateTaskProgress,
        onDeleteTaskClick = viewModel::deleteTask,
        onNavigateToEdit = onNavigateToEdit,
        onQueryChange = viewModel::updateQuery,
        onProgressFilterChange = viewModel::updateProgressFilter,
        onSortingTypeChange = viewModel::updateSortingType,
        onSync = viewModel::sync,
        onNavigateToTasks = onNavigateToTasks,
        onUnArchiveTaskClick = viewModel::unArchiveTask,
        wasOfflineDialogShown = wasOfflineDialogShown,
        onOfflineDialogDismiss = onOfflineDialogDismiss,
        isOnline = isOnline,
        onMarkSyncedOnceAsTrue = viewModel::markSyncedOnceAsTrue
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
    onSync: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onUnArchiveTaskClick: (Task) -> Unit,
    wasOfflineDialogShown: Boolean,
    onOfflineDialogDismiss: () -> Unit,
    isOnline: Boolean?,
    onMarkSyncedOnceAsTrue: () -> Unit
) {

    //TODO: This logic should be in the tasks repository because it's not UI logic and this logic is used also in the archive screen. Cancel the coroutine with the syncing when there's no internet connection and mark syncedOnce to true. If there's no internet initially mark syncedOnce as true. I think the Job class must be involved.
    // Learn async programming in more depth and then fix. Inject network monitor to the repository
    LaunchedEffect(isOnline) {
        if (isOnline == true) {
            while (true) {
                onSync()
                delay(3_500.milliseconds)
            }
        } else if (isOnline == false){
            onMarkSyncedOnceAsTrue()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!wasOfflineDialogShown && isOnline != true && isOnline != null) {
            OfflineAlertDialog(onOfflineDialogDismiss = onOfflineDialogDismiss)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                    ErrorMessage(uiState.message)
                }

                is TasksUiState.Success -> {
                    val expandedSearchBar = uiState.query != ""
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TasksDockedSearchBar(
                            visibleTasks = uiState.visibleTasks,
                            onTaskProgressChange = onTaskProgressChange,
                            onDeleteTaskClick = onDeleteTaskClick,
                            onNavigateToEdit = onNavigateToEdit,
                            onQueryChange = onQueryChange,
                            query = uiState.query,
                            modifier = Modifier
                                .weight(1f),
                            onArchiveTaskClick = {},
                            onUnarchiveTaskClick = onUnArchiveTaskClick,
                            areTasksArchived = true
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    if (!expandedSearchBar) {
                        Row {
                            FilterSortTasksButtons(
                                sortingType = uiState.sortingType,
                                onSortingTypeChange = onSortingTypeChange,
                                onProgressFilterChange = onProgressFilterChange,
                                progressFilter = uiState.progressFilter
                            )
                        }
                        Spacer(modifier = Modifier.height(8.5.dp))

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
                                        onArchiveTaskClick = {},
                                        onUnarchiveTaskClick = onUnArchiveTaskClick,
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