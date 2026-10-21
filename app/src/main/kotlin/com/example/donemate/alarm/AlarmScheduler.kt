package com.example.donemate.alarm

interface AlarmScheduler {
    fun setReminder(time: Long?, id: String)
    fun cancelReminder(id: String)
}