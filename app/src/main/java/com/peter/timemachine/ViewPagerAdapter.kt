package com.peter.timemachine

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> StopwatchFragment()
            1 -> TimerFragment()
            else -> StopwatchFragment()
        }
    }
}