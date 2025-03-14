package com.example.myappplaylistmaker.presentation.ui.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myappplaylistmaker.presentation.ui.library.fav_tracks.FavTracksFragment
import com.example.myappplaylistmaker.presentation.ui.library.playlists.PlaylistFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle)  {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavTracksFragment()
            1 -> PlaylistFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}