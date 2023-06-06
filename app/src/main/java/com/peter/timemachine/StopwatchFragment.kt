package com.peter.timemachine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.peter.timemachine.databinding.FragmentStopwatchBinding
import kotlin.math.roundToInt

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!
    private var timerStarted = false
    private lateinit var serviceIntent : Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceIntent = Intent(activity?.applicationContext, TimerService::class.java)
        activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        binding.btnStartStopwatch.setOnClickListener {
            binding.btnStartStopwatch.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            binding.btnStartStopwatch.visibility = View.GONE
            binding.btnPauseStopwatch.visibility = View.VISIBLE
            binding.btnStopStopwatch.visibility = View.VISIBLE

            startTimer()
        }

        binding.btnStopStopwatch.setOnClickListener {
            binding.btnStopStopwatch.performHapticFeedback(HapticFeedbackConstants.REJECT)
            binding.btnPauseStopwatch.visibility = View.GONE
            binding.btnStopStopwatch.visibility = View.GONE
            binding.btnStartStopwatch.visibility = View.VISIBLE

            resetTimer()
        }

        binding.btnPauseStopwatch.setOnClickListener {
            binding.btnPauseStopwatch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (timerStarted) {
                binding.btnPauseStopwatch.setImageResource(R.drawable.ic_start)
                pauseTimer()
            } else {
                binding.btnPauseStopwatch.setImageResource(R.drawable.ic_pause)
                startTimer()
            }

        }
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        activity?.startService(serviceIntent)
        timerStarted = true
    }

    private fun pauseTimer() {
        activity?.stopService(serviceIntent)
        timerStarted = false
    }
    private fun resetTimer() {
        pauseTimer()
        time = 0.0
        binding.timerText.text = getTimeStringFromDouble(time)
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            }
            binding.timerText.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val result = time.roundToInt()
        val hours = result % 86400 / 3600
        val minutes = result % 86400 % 3600 / 60
        val seconds = result % 86400 % 3600 % 60
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}