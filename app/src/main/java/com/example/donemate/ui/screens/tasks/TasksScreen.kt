package com.example.donemate.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.model.Task
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.example.donemate.model.Progress

@Composable
fun TasksScreen(
    vm: TasksViewModel,
    navigateToEdit: (task: String) -> Unit,
    navigateToAdd: () -> Boolean
) {
    val tasks by vm.tasks.collectAsStateWithLifecycle(emptyList())
    Box(modifier =  Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(
                    task,
                    onTaskCheckChange = vm::onTaskCheckChange,
                    onDeleteTask = vm::onDeleteTask,
                    navigateToEdit = navigateToEdit
                )
            }
        }
        FloatingActionButton(
            onClick = {navigateToAdd()},
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
    onTaskCheckChange: (Task, Int) -> Unit,
    onDeleteTask: (Task) -> Unit,
    navigateToEdit: (task: String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    val menuItemData: Map<String, Int> = Progress.entries.associate { it.label to it.value }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(8.dp))
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
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Progress.labelFromInt(task.progress),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            menuItemData.forEach { (label, value) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onTaskCheckChange(task, value)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Column(modifier = Modifier.wrapContentSize()) {
                    Button(onClick = {onDeleteTask(task)},
                        modifier = Modifier.width(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935),
                            contentColor = Color.White
                        )){
                        Text("Delete")
                    }
                    Button(onClick = { navigateToEdit(task.id) },
                        modifier = Modifier.width(100.dp)){
                        Text("Edit")
                    }
                }
            }
        }
    }
}