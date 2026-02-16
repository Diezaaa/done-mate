package com.example.donemate.domain.alarms

import com.example.donemate.domain.local.AlarmsRepository


interface AlarmScheduler{
    fun insert(taskId: String)
    fun edit(taskId: String)

    fun delete(taskId: String)
}