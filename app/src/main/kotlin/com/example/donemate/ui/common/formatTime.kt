package com.example.donemate.ui.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

// TODO: Re-read this code and understand it again

private val reminderTimeFormat = LocalTime.Format {
    hour()
    char(':')
    minute()
}

private val reminderDateFormat = LocalDate.Format {
    day(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
}

fun Instant.formatTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    now: Instant = Clock.System.now()
): String {
    val local = toLocalDateTime(timeZone)
    val date = local.date
    val today = now.toLocalDateTime(timeZone).date

    val dayLabel = when (today.daysUntil(date)) {
        -1 -> "yesterday"
        0 -> "today"
        1 -> "tomorrow"
        else -> {
            val text = reminderDateFormat.format(date)
            if (date.year == today.year) text else "$text ${date.year}"
        }
    }

    return "$dayLabel at ${reminderTimeFormat.format(local.time)}"
}