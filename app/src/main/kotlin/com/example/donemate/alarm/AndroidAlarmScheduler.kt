package com.example.donemate.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.donemate.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.time.Clock

@Singleton
class AndroidAlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) :
    AlarmScheduler {
    private val alarmMgr: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun setReminder(time: Long?, id: String) {
        if (time == null) return
        val currentTime = Clock.System.now().toEpochMilliseconds()
        cancelReminder(id)
        if (time <= currentTime) {
            return
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val clockInfo = AlarmManager.AlarmClockInfo(time, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmMgr.canScheduleExactAlarms()) {
                alarmMgr.setAlarmClock(clockInfo, pendingIntent)
            }
        } else {
            alarmMgr.setAlarmClock(clockInfo, pendingIntent)
        }
    }

    override fun cancelReminder(id: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmMgr.cancel(pendingIntent)
    }
}