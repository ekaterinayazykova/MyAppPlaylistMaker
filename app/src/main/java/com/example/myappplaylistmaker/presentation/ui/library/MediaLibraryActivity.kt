package com.example.myappplaylistmaker.presentation.ui.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.ActivityLibraryBinding
import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {

    private var _binding: ActivityLibraryBinding? = null
    private val binding get() = _binding!!
    private val playlistViewModel by viewModel<PlaylistViewModel>()
    private val favTracksViewModel by viewModel<FavTracksViewModel>()
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.arrow.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = ViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.fav_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabLayoutMediator.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("MediaLibrary", "Tab selected: ${tab?.position}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        Log.d("MediaLibrary", "Mediator attached")
        binding.tabLayout.requestLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

}
