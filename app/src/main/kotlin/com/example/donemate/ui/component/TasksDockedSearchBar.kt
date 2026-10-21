package com.example.donemate.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.donemate.data.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksDockedSearchBar(
    visibleTasks: List<Task>,
    onTaskProgressChange: (Task, Int) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    query: String,
    onArchiveTaskClick: (Task) -> Unit,
    onUnarchiveTaskClick: (Task) -> Unit,
    modifier: Modifier,
    areTasksArchived: Boolean
) {
    val expanded = query.isNotEmpty()
    DockedSearchBar(
        modifier = modifier,
        expanded = expanded,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    onQueryChange(it)
                },
                onSearch = {},
                expanded = expanded,
                onExpandedChange = {
                },
                placeholder = { Text("Search for a task") },
                leadingIcon = {
                    if (!expanded) {
                        Icon(Icons.Default.Search, "Search")
                    } else {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                }
            )
        },
        onExpandedChange = {

        }
    ) {
        if (expanded) {
            LazyColumn {
                items(visibleTasks, key = { it.id }) { task ->
                    Box(
                        modifier = Modifier.padding(9.dp, 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TaskItem(
                            task,
                            onTaskProgressChange = onTaskProgressChange,
                            onDeleteTaskClick = onDeleteTaskClick,
                            onNavigateToEdit = onNavigateToEdit,
                            onArchiveTaskClick = onArchiveTaskClick,
                            onUnarchiveTaskClick = onUnarchiveTaskClick,
                            isTaskArchived = areTasksArchived
                        )
                    }
                }
            }
        }
    }
}