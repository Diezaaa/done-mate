package com.example.donemate.ui.screens.task

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.data.model.Progress
import com.example.donemate.ui.common.TaskFormUiState
import com.example.donemate.ui.component.CameraOrGalleryDialog
import com.example.donemate.ui.component.CustomLoadingIndicator
import com.example.donemate.ui.component.DateTimePickerField
import com.example.donemate.ui.component.TaskImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskScreen(
    vm: TaskViewModel,
    id: String?,
    onNavigateToTasks: () -> Unit,
    onNavigateToPermission: (String) -> Unit,
    onNavigateToPreview: (String) -> Unit,
    cameraImageUri: String?,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (id != null) {
            vm.loadTask(id)
        }
    }


    LaunchedEffect(uiState) {
        if (uiState is TaskFormUiState.Saved) {
            onNavigateToTasks()
        }
    }

    TaskScreen(
        uiState = uiState,
        onNavigateToTasks = onNavigateToTasks,
        onTitleChange = vm::updateTitle,
        onProgressChange = vm::updateProgress,
        onSaveClick = vm::saveTask,
        onReminderDateTimeChange = vm::updateReminderDateTime,
        onNavigateToPermission = onNavigateToPermission,
        onNavigateToPreview = onNavigateToPreview,
        cameraImageUri = cameraImageUri,
        onImageChange = vm::updateImageUri,
        onDueDateTimeChange = vm::updateDueDateTime,
        onDescriptionChange = vm::updateDescription
    )
}

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun TaskScreen(
    onReminderDateTimeChange: (Long?) -> Unit,
    onTitleChange: (String) -> Unit,
    onProgressChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateToPermission: (String) -> Unit,
    onNavigateToPreview: (String) -> Unit,
    cameraImageUri: String?,
    onImageChange: (String) -> Unit,
    uiState: TaskFormUiState,
    onNavigateToTasks: () -> Unit,
    onDueDateTimeChange: (Long?) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    val menuItemData: Map<String, Int> = Progress.entries.associate { it.label to it.value }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cameraImageUri) {
        if (cameraImageUri != null) {
            onImageChange(cameraImageUri)
        }
    }


    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                onImageChange(uri.toString())
            }
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {

        if (uiState is TaskFormUiState.Error) {
            Text(text = uiState.message)
        }
        if (uiState is TaskFormUiState.Loading) {
            CustomLoadingIndicator()
        }
    }
    if (uiState is TaskFormUiState.Loaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigateToTasks() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }


                if (uiState.task.imageUri != null) {

                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter
                    ) {

                        uiState.task.imageUri?.let { imageUri ->
                            TaskImage(imageUri)
                        }

                    }
                }




                Spacer(Modifier.height(15.dp))
                TextField(
                    value = uiState.task.title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !uiState.isValidForm,
                    supportingText = {
                        if (!uiState.isValidForm) {
                            Text(
                                "Title cannot be empty", color = MaterialTheme.colorScheme.error
                            )
                        }
                    })
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = uiState.task.description ?: "",
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        .height(40.dp), contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(37.dp)
                    ) {
                        Text(
                            text = Progress.labelFromInt(uiState.task.progress),
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
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        menuItemData.forEach { (label, value) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = {
                                onProgressChange(value)
                                expanded = false
                            })
                        }
                    }
                }

                val cameraPermissionState = rememberPermissionState(
                    Manifest.permission.CAMERA
                )

                if (showImagePickerDialog) {
                    CameraOrGalleryDialog(
                        onDismissRequest = { showImagePickerDialog = false },
                        onCameraOptionClick = {
                            if (!cameraPermissionState.status.isGranted) {
                                onNavigateToPermission(uiState.task.title)
                            } else {
                                onNavigateToPreview(uiState.task.title)
                            }
                        },
                        onGalleryOptionClick = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        })
                }
                Spacer(Modifier.height(8.dp))

                DateTimePickerField(
                    modifier = Modifier.fillMaxWidth(),
                    dateTime = uiState.task.dueDateTime,
                    onDateTimeChange = onDueDateTimeChange,
                    label = "Due",
                    isAlarm = false
                )

                Spacer(Modifier.height(8.dp))

                DateTimePickerField(
                    modifier = Modifier.fillMaxWidth(),
                    dateTime = uiState.task.reminderDateTime,
                    onDateTimeChange = onReminderDateTimeChange,
                    label = "Reminder",
                    isAlarm = true
                )


                Spacer(Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showImagePickerDialog = true }) {
                    Text("Set image")
                }
                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSaveClick()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(56.5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done, contentDescription = "Back"
                    )
                    Spacer(Modifier.width(13.dp))
                    Text(
                        "Save task"
                    )
                }
            }
        }
    }
}

