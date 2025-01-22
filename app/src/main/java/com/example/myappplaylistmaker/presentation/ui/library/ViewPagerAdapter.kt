package com.example.myappplaylistmaker.presentation.ui.library

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle)  {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("ViewPagerAdapter", "Creating fragment for position: $position")
        return when(position) {
            0 -> FavTracksFragment()
            1 -> PlaylistFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}