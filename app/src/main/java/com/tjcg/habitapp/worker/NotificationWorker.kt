package com.tjcg.habitapp.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tjcg.habitapp.R
import java.util.*

class NotificationWorker(val ctx: Context, val workerParams: WorkerParameters) :
    Worker(ctx, workerParams) {

    override fun doWork(): Result {
        val cal = Calendar.getInstance()
        val dayInt = cal.get(Calendar.DAY_OF_WEEK)
        val isTodayActive = workerParams.inputData.getBoolean("$ACTIVE-$dayInt", false)
        Log.d("HabitWorker", "Notification active on $dayInt : $isTodayActive")
        if(isTodayActive) {
            showNotification()
        }
        return Result.success()
    }

    private fun showNotification() {
        val nManager = ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            nManager.createNotificationChannel(channel)
            val notification = Notification.Builder(ctx, CHANNEL_ID).apply {
                setContentTitle(workerParams.inputData.getString(NOTIFICATION_TITLE))
                setContentText(workerParams.inputData.getString(NOTIFICATION_SUBTITLE))
                setSmallIcon(R.drawable.habit_icon_round)
            }.build()
            nManager.notify(0, notification)
        } else {
            val notification = Notification.Builder(ctx)
                .setContentTitle(workerParams.inputData.getString(NOTIFICATION_TITLE))
                .setContentText(workerParams.inputData.getString(NOTIFICATION_SUBTITLE))
                .setSmallIcon(R.drawable.habit_icon_round)
                .build()
            nManager.notify(0, notification)
        }
    }

    companion object {
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_SUBTITLE = "notification_sub"
        const val CHANNEL_ID = "HabitChannel"
        const val CHANNEL_NAME = "HabitChannel"
        const val ACTIVE = "active" // -int  e.g. active-1   1 means on sunday
    }
}