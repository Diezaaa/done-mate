package com.example.donemate.ui.common

import com.example.donemate.data.model.Task

fun List<Task>.searchFilterAndSortTasks(
    query: String,
    progressFilter: Int,
    sortingType: SortingType
): List<Task> =
    this.filter { task ->
            task.title.contains(query, ignoreCase = true) ||
                    task.description?.contains(query, ignoreCase = true) == true
        }
        .let { searchedTasks ->
            if (query.isNotBlank()) {
                searchedTasks
            } else {
                searchedTasks.filter { task ->
                    progressFilter == -1 ||
                            task.progress == progressFilter
                }
            }
        }
        .let { filteredTasks ->
            when (sortingType) {
                SortingType.TimeUpdated ->
                    filteredTasks.sortedByDescending { it.updateTime }

                SortingType.ReminderDateTime ->
                    filteredTasks.sortedByDescending { it.reminderDateTime }

                SortingType.DueDateTime ->
                    filteredTasks.sortedByDescending { it.dueDateTime }
            }
        }