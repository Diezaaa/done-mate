package com.example.donemate.ui.component

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import androidx.core.net.toUri

@SuppressLint("ServiceCast")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalPermissionsApi::class)
@Composable
fun DateTimePickerField(
    dateTime: Instant?,
    onDateTimeChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    isAlarm: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var timeInMs by remember { mutableStateOf<Long?>(null) }

    val alarmPermissionState = if (Build.VERSION.SDK_INT >= 33) {
        rememberPermissionState(SCHEDULE_EXACT_ALARM)
    } else null

    val isAlarmPermissionGranted = alarmPermissionState?.status?.isGranted ?: true


    val notificationPermissionState = if (Build.VERSION.SDK_INT >= 33) {
        rememberPermissionState(POST_NOTIFICATIONS)
    } else null
    val isNotificationPermissionGranted = notificationPermissionState?.status?.isGranted ?: true

    Box {
        OutlinedTextField(
            enabled = if (isAlarm) isAlarmPermissionGranted else true,
            value = dateTime?.let {
                LocalDateTime.ofInstant(it, ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
            } ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                if (isAlarm) {
                    Icon(Icons.Default.Alarm, contentDescription = null)
                } else {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .then(
                    Modifier.pointerInput(dateTime) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent =
                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) showDatePicker = true
                        }
                    }

                )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    if (isAlarm) {
                        if (!isAlarmPermissionGranted) return@clickable
                        if (isNotificationPermissionGranted) {
                            notificationPermissionState?.launchPermissionRequest()
                        }
                    }
                    showDatePicker = true

                }
        )
    }
    val context = LocalContext.current
    val packageName = context.packageName
    if (isAlarm) {
        if (!isAlarmPermissionGranted) {
            Text("Grant the alarm permission to set a notification")
            Text("Go to settings and enable the permission", modifier = Modifier.clickable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(
                        ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        "package:$packageName".toUri()
                    )
                    context.startActivity(intent)
                }
            })
        }
        if (isAlarmPermissionGranted && !isNotificationPermissionGranted) {
            Text("Grant the notification permission to set a notification to get reminders")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    timeInMs = datePickerState.selectedDateMillis
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    timeInMs?.let { dateMillis ->
                        val localDate = Instant.ofEpochMilli(dateMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

                        val localDateTime = LocalDateTime.of(localDate, localTime)

                        val finalInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()

                        onDateTimeChange(finalInstant.toEpochMilli())
                    }
                    showTimePicker = false
                }) { Text("OK") }
            },
            title = { Text("Time") }
        ) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}