package com.peter.timemachine

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peter.timemachine.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNumberPicker()
        setupButtons()
        binding.npHours.maxValue = 23
        binding.npMinutes.maxValue = 59
        binding.npSeconds.maxValue = 59

        binding.npHours.minValue = 0
        binding.npMinutes.minValue = 0
        binding.npSeconds.minValue = 0
    }

    private fun setupNumberPicker() {
        binding.npHours.setOnValueChangedListener { picker, _, _ ->
            picker.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        binding.npMinutes.setOnValueChangedListener { picker, _, _ ->
            picker.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        binding.npSeconds.setOnValueChangedListener { picker, _, _ ->
            picker.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    private fun setupButtons() {
        binding.btnStartTimer.setOnClickListener {
            binding.btnStartTimer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        binding.btnPauseTimer.setOnClickListener {
            binding.btnStartTimer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        binding.btnStopTimer.setOnClickListener {
            binding.btnStartTimer.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}