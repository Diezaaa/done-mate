package com.example.donemate.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.donemate.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id")

        Log.d("AlarmReceiver", "Received ID: $id")
        notificationHelper.setUpNotificationChannel()
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {

            if (id != null) {
                notificationHelper.createMessageAndShow(id)
            }
            pendingResult.finish()

        }
    }
}