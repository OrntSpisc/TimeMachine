package com.peter.timemachine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.timemachine.databinding.FragmentStopwatchBinding

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!
    private lateinit var statusReceiver: BroadcastReceiver
    private lateinit var timeReceiver: BroadcastReceiver
    private var isRunning = false
    private var isStarted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartStopwatch.setOnClickListener {
            binding.btnStartStopwatch.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            startTimer()
        }

        binding.btnStopStopwatch.setOnClickListener {
            binding.btnStopStopwatch.performHapticFeedback(HapticFeedbackConstants.REJECT)


            resetTimer()
        }

        binding.btnPauseStopwatch.setOnClickListener {
            binding.btnPauseStopwatch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            if (isRunning) {
                binding.btnPauseStopwatch.setImageResource(R.drawable.ic_start)
                pauseTimer()
            } else {
                binding.btnPauseStopwatch.setImageResource(R.drawable.ic_pause)
                startTimer()
            }
        }
    }

    private fun resetTimer() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "RESET")
        requireActivity().startService(stopwatchService)
        updateStopwatchValue(0)
        isRunning = false
        isStarted = false

        defaultTImerLayout()
    }

    private fun defaultTImerLayout() {
        binding.btnPauseStopwatch.visibility = View.GONE
        binding.btnStopStopwatch.visibility = View.GONE
        binding.btnStartStopwatch.visibility = View.VISIBLE
    }

    private fun startTimer() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "START")
        requireActivity().startService(stopwatchService)
        isRunning = true
        isStarted = true

        startedTimerLayout()
    }

    private fun startedTimerLayout() {
        binding.btnStartStopwatch.visibility = View.GONE
        binding.btnPauseStopwatch.visibility = View.VISIBLE
        binding.btnStopStopwatch.visibility = View.VISIBLE
    }

    private fun pauseTimer() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "PAUSE")
        requireActivity().startService(stopwatchService)

        isRunning = false
    }


    override fun onResume() {
        super.onResume()

        getStopwatchStatus()
        val statusFilter = IntentFilter()
        statusFilter.addAction("STOPWATCH_STATUS")
        statusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isRunning = intent?.getBooleanExtra(StopwatchService.ISRUNNING, false)!!

                updateLayout(isRunning)
            }
        }
        requireActivity().registerReceiver(statusReceiver, statusFilter)

        val timeFilter = IntentFilter()
        timeFilter.addAction("STOPWATCH_TICK")
        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val timeElapsed = intent?.getIntExtra(StopwatchService.TIME_ELAPSED, 0)!!
                updateStopwatchValue(timeElapsed)
            }
        }
        requireActivity().registerReceiver(timeReceiver, timeFilter)
    }

    private fun updateLayout(running: Boolean) {
        if (running) {
            startedTimerLayout()
        } else if (!running && isStarted) {
            binding.btnPauseStopwatch.setImageResource(R.drawable.ic_start)
        } else if (!running && !isStarted) {
            defaultTImerLayout()
        }
    }

    private fun updateStopwatchValue(timeElapsed: Int) {
        val hours: Int = timeElapsed / 60 / 60
        val minutes: Int = timeElapsed / 60
        val seconds: Int = timeElapsed % 60
        binding.timerText.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    private fun getStopwatchStatus() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "GET_STATUS")
        requireActivity().startService(stopwatchService)
    }

    private fun moveToForeground() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "MOVE_TO_FOREGROUND")
        requireActivity().startService(stopwatchService)
    }

    private fun moveToBackground() {
        val stopwatchService = Intent(activity, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, "MOVE_TO_BACKGROUND")
        requireActivity().startService(stopwatchService)
    }

    override fun onStart() {
        super.onStart()
        if (isRunning) {
            moveToBackground()
        }
    }

    override fun onPause() {
        super.onPause()

        requireActivity().unregisterReceiver(statusReceiver)
        requireActivity().unregisterReceiver(timeReceiver)

        moveToForeground()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}