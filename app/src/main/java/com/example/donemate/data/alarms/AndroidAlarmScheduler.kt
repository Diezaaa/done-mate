package com.example.donemate.data.alarms

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.donemate.domain.alarms.AlarmScheduler
import com.example.donemate.domain.local.AlarmsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAlarmScheduler @Inject constructor(
    private val context: Application
): AlarmScheduler {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override fun insert(taskId: String) {
        val pendingIntent =
            PendingIntent.getService(context, taskId, intent,
                PendingIntent.FLAG_NO_CREATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() ?: false) {
                alarmManager.setAlarmClock()
            }
        }
        else{
            alarmManager.setAlarmClock(alarmManager.ELAPSED_REALTIME_WAKEUP)

        }
    }

    override fun edit() {
    }

    override fun delete() {
        
    }
}