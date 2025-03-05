package com.example.myappplaylistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentLibraryBinding
import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val playlistViewModel by viewModel<PlaylistViewModel>()
    private val favTracksViewModel by viewModel<FavTracksViewModel>()
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle
        )

        tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.fav_tracks)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }
}