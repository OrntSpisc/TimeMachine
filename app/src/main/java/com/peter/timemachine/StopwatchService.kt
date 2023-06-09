package com.peter.timemachine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Timer
import java.util.TimerTask

class StopwatchService : Service() {
    private var isRunning = false
    private var timeElapsed: Int = 0
    private lateinit var timer: Timer
    private lateinit var updateTimer: Timer
    private lateinit var notificationManager: NotificationManager


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        getNotificationManager()

        val action = intent?.getStringExtra(STOPWATCH_ACTION)

        when (action) {
            "START" -> startStopwatch()
            "PAUSE" -> pauseStopwatch()
            "RESET" -> resetStopwatch()
            "GET_STATUS" -> getStatus()
            "MOVE_TO_FOREGROUND" -> moveToForeground()
            "MOVE_TO_BACKGROUND" -> moveToBackground()
        }

        return START_STICKY
    }

    private fun resetStopwatch() {
        pauseStopwatch()
        timeElapsed = 0
        isRunning = false
        getStatus()
    }


    private fun startStopwatch() {
        isRunning = true
        timer = Timer()

        getStatus()

        timer.scheduleAtFixedRate(object : TimerTask()
        {
            override fun run() {
                val intent = Intent("STOPWATCH_TICK")
                timeElapsed++

                intent.putExtra(TIME_ELAPSED, timeElapsed)
                sendBroadcast(intent)
            }
        },
            0, 1000)

    }

    private fun pauseStopwatch() {
        timer.cancel()
        isRunning = false
        getStatus()
    }

    private fun getStatus() {
        val statusIntent = Intent()
        statusIntent.action = "STOPWATCH_STATUS"
        statusIntent.putExtra(ISRUNNING, isRunning)
        statusIntent.putExtra(TIME_ELAPSED, timeElapsed)
        sendBroadcast(statusIntent)
    }

    private fun moveToForeground() {
        if (isRunning) {
            startForeground(1, buildNotification())
            updateTimer = Timer()

            updateTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            },
            0, 1000)
        }
    }
    //Update notification
    private fun updateNotification() {
        notificationManager.notify(1, buildNotification())
    }

    private fun buildNotification() : Notification {
        val title = if (isRunning) {
            "Stopwatch is running!"
        } else {
            "Stopwatch is paused!"
        }

        val hours: Int = timeElapsed / 60 / 60
        val minutes: Int = timeElapsed / 60
        val seconds: Int = timeElapsed % 60

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
            )
            .setColorized(true)
            .setColor(resources.getColor(R.color.blue, theme))
            .setSmallIcon(R.drawable.ic_stopwatch)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Stopwatch",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.setSound(null, null)
        notificationChannel.setShowBadge(true)
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }


    companion object {
        const val NOTIFICATION_CHANNEL_ID = "Stopwatch_Notifications"

        const val ISRUNNING = "ISRUNNING"
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
    }
}