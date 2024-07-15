package com.example.fitapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Logic to check if the user needs to log an activity
        NotificationHelper.sendNotification(
            applicationContext,
            "Log Your Activity",
            "Don't forget to log your activities today!"
        )
        return Result.success()
    }
}
