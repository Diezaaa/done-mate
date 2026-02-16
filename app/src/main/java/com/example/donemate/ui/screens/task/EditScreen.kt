package com.example.donemate.ui.screens.task

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.donemate.domain.model.Progress
import com.example.donemate.ui.components.CameraOrGalleryDialog
import com.example.donemate.ui.theme.DoneMateTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditScreen(
    id: String,
    vm: EditViewModel,
    navigateToTasks: () -> Boolean,
    navigateToPermission: () -> Unit,
    onNavigateToPreview: () -> Unit,
    cameraImageUri: String?
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        vm.loadTask(id)
    }

    EditScreenContent(
        title = uiState.title,
        progress = uiState.progress,
        dateTime = uiState.dateTime,
        onTitleChange = vm::onTitleChange,
        onProgressChange = vm::onProgressChange,
        onSaveTask = vm::onSaveTask,
        onDateTimeChange = vm::onDateTimeChange,
        id = id,
        navigateToTasks = navigateToTasks,
        navigateToPermission = navigateToPermission,
        onNavigateToPreview = onNavigateToPreview,
        cameraImageUri = cameraImageUri
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditScreenContent(
    title: String,
    progress: Int,
    dateTime: Instant?,
    onDateTimeChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    onTitleChange: (String) -> Unit,
    onProgressChange: (Int) -> Unit,
    onSaveTask: (String?, String) -> Unit,
    id: String,
    navigateToTasks: () -> Boolean,
    navigateToPermission: () -> Unit,
    onNavigateToPreview: () -> Unit,
    cameraImageUri: String?
) {
    val menuItemData: Map<String, Int> = Progress.entries.associate { it.label to it.value }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf(cameraImageUri) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri.toString()
        }
    }
    var timeInMs by remember { mutableStateOf<Long?>(null) }


    Row(modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Edit Task",
                fontSize = 35.sp
            )
            Spacer(Modifier.height(15.dp))

            TextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") }
            )
            Spacer(Modifier.height(8.dp))

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
                        text = Progress.labelFromInt(progress),
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
                                onProgressChange(value)
                                expanded = false
                            }
                        )
                    }
                }
            }
            val cameraPermissionState = rememberPermissionState(
                Manifest.permission.CAMERA
            )

            if (showImagePickerDialog) {
                CameraOrGalleryDialog(
                    onDismissRequest = { showImagePickerDialog = false },
                    onChoosingCamera = {
                        if (!cameraPermissionState.status.isGranted){
                            navigateToPermission()
                        }
                        else{
                            onNavigateToPreview()
                        }
                    },
                    onChoosingGallery = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
            val context = LocalContext.current
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
            OutlinedTextField(
                enabled = canScheduleExact,
                value = if (dateTime != null)
                    LocalDateTime.ofInstant(dateTime, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm"))
                else
                    "",
                onValueChange = { },
                label = { Text("Date and time") },
                trailingIcon = {
                    Icon(Icons.Default.Alarm, contentDescription = "Select date")
                },
                modifier = modifier
                    .fillMaxWidth()
                    .then(
                    if (canScheduleExact) {
                            Modifier.pointerInput(dateTime) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        showDatePicker = true
                                    }
                                }
                            }
                        } else {
                            Modifier
                        }
                    )
            )

            if (!canScheduleExact){
                Text("Grant the alarm permission to set a notification")
            }
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                timeInMs = datePickerState.selectedDateMillis
                                showDatePicker = false
                                showTimePicker = true
                            },
                            enabled = showDatePicker,
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    },
                ){
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            }
            if (showTimePicker){
                val timePickerState = rememberTimePickerState()

                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                timeInMs = timeInMs?.plus((timePickerState.hour * 3600000) + (timePickerState.minute * 1000))
                                onDateTimeChange(
                                    timeInMs
                                )
                                showTimePicker = false
                            },
                            enabled = showTimePicker
                        ) {
                            Text("OK")
                        }
                    },
                    title = { Text("Time") }
                ) {
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState()))
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showImagePickerDialog = true}){
                Text("Set image")
            }
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {onSaveTask(imageUri, id)
                    navigateToTasks()},
                modifier = Modifier.width(283.dp)
            ) {
                Text("Update")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    DoneMateTheme {
        EditScreenContent(
            title = "Sample Task",
            progress = 1,
            dateTime = null,
            onTitleChange = {},
            onProgressChange = {},
            onSaveTask = { _, _ -> },
            id = "123",
            navigateToTasks = { true },
            navigateToPermission = {},
            onNavigateToPreview = {},
            cameraImageUri = null,
            onDateTimeChange = {}
        )
    }
}