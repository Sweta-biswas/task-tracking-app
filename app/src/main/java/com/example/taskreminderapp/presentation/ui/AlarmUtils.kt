package com.example.taskreminderapp.presentation.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.example.taskreminderapp.domain.model.Reminder
import com.example.taskreminderapp.presentation.ReminderReceiver


const val REMINDER = "REMINDER"

fun setUpAlarm(context: Context, reminder: Reminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.timeInMillis.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.timeInMillis,
                    pendingIntent
                )
            } else {
                // Handle case where exact alarms are not allowed
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.timeInMillis,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        Log.e("AlarmUtils", "Failed to set alarm", e)
    }
}

fun cancelAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, reminder.timeInMillis.toInt(),
        intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.cancel(pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

fun setUpPeriodicAlarm(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, reminder.timeInMillis.toInt(),
        intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        val interval = 2L * 60 * 1000 // 2 minutes in milliseconds
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            reminder.timeInMillis,
            interval,
            pendingIntent)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}