package com.example.moveon.ui.guide

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GuidePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = GuideViewModel.PAGE_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GuideFragment1()
            1 -> GuideFragment2()
            2 -> GuideFragment3()
            3 -> GuideFragment4()
            else -> throw IllegalArgumentException("Invalid guide page position: $position")
        }
    }
}
