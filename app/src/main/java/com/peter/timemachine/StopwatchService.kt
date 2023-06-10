package com.peter.timemachine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Timer
import java.util.TimerTask

class StopwatchService : Service() {
    private var isRunning = false
    private var timeElapsed: Double = 0.0
    private lateinit var timer: Timer
    private lateinit var updateTimer: Timer
    private lateinit var notificationManager: NotificationManager


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Intiializer notification channel and manager
        createNotificationChannel()
        getNotificationManager()

        //Get action extra from fragment
        val action = intent?.getStringExtra(STOPWATCH_ACTION)

        //Action keyword conditions
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
        timeElapsed = 0.0
        isRunning = false
        getStatus()
    }


    private fun startStopwatch() {
        isRunning = true

        //Initialize timer here for pausing ability
        timer = Timer()

        getStatus()

        //Start timer object
        timer.scheduleAtFixedRate(object : TimerTask()
        {
            override fun run() {
                //Do every 10 milliseconds
                //Send broadcast of time elapsed with "STOPWATCH_TICK" action
                val intent = Intent("STOPWATCH_TICK")
                timeElapsed += 0.01
                intent.putExtra(TIME_ELAPSED, timeElapsed)
                sendBroadcast(intent)
            }
        },
            0, 10)

    }

    private fun pauseStopwatch() {
        timer.cancel()
        isRunning = false
        getStatus()
    }

    private fun getStatus() {
        //Broadcast stopwatch status (running state and time elapsed)
        val statusIntent = Intent()
        statusIntent.action = "STOPWATCH_STATUS"
        statusIntent.putExtra(ISRUNNING, isRunning)
        statusIntent.putExtra(TIME_ELAPSED, timeElapsed)
        sendBroadcast(statusIntent)
    }

    //Start foreground service when fragment is closed
    private fun moveToForeground() {
        if (isRunning) {
            startForeground(1, buildNotification())

            //Create new timer object for notification
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
        //Send notification with id: 1
        notificationManager.notify(1, buildNotification())
    }

    private fun buildNotification() : Notification {
        //Stopwatch state notification title
        val title = if (isRunning) {
            "Stopwatch is running!"
        } else {
            "Stopwatch is paused!"
        }

        //Convert seconds to time
        var hours: Int = (timeElapsed / 60 / 60).toInt()
        var minutes: Int = (timeElapsed / 60).toInt()
        if (minutes > 59) {
            hours++
            minutes = 0
        }
        val seconds: Int = (timeElapsed % 60).toInt()

        //PendingIntent for handling notification click
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        //Return a Notification
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

    //Stop foreground service when reopened fragment
    private fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }


    //Create notification channel
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

    //Get notification manager from systemservice
    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    //Intent keywords
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "Stopwatch_Notifications"

        const val ISRUNNING = "ISRUNNING"
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val TIME_ELAPSED = "TIME_ELAPSED"
    }
}