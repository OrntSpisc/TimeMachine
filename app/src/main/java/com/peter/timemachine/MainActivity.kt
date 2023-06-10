package com.peter.timemachine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.timemachine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing ViewPager and Tablayout
        viewPager = binding.ViewPager
        tabLayout = binding.TabLayout
        setupViewPager2()
    }

    //Setup ViewPager with fragments
    private fun setupViewPager2() {
        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position) {
                0 -> tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_stopwatch)
                else -> tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_timer)
            }
        }.attach()
    }

    //Return to first fragment on back pressed
    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem--
        }
    }
}