package com.example.myappplaylistmaker.presentation.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplaylistmaker.databinding.FragmentFavtrackBinding
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.myappplaylistmaker.presentation.ui.search.UnifiedTrackAdapter
import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import com.example.myappplaylistmaker.presentation.view_models.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavTracksFragment : Fragment() {

    private var _binding: FragmentFavtrackBinding? = null
    private val binding get() = _binding!!
    private val favTracksViewModel by viewModel<FavTracksViewModel>()
    private lateinit var unifiedTrackAdapter: UnifiedTrackAdapter
    private var isDebounceEnabled = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavtrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unifiedTrackAdapter = UnifiedTrackAdapter(mutableListOf()) { track ->
            openTrack(track)
        }

        binding.favTrackList.layoutManager = LinearLayoutManager(requireContext())
        binding.favTrackList.adapter = unifiedTrackAdapter

        observeState()
        favTracksViewModel.getFavTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openTrack(track: Track) {
        val trackIntent = Intent(requireActivity(), MediaPlayerActivity::class.java)
        trackIntent.putExtra(MediaPlayerActivity.TRACK_DATA, track)
        ContextCompat.startActivity(requireContext(), trackIntent, null)
    }

    fun debouncedClick() {

    }

    private fun observeState() {
        favTracksViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavTracksViewModel.State.EmptyFavTracks -> {
                    binding.favTrackList.visibility = View.GONE
                    binding.noSongPlaceholder.visibility = View.VISIBLE
                    binding.emptyFav.visibility = View.VISIBLE
                }

                is FavTracksViewModel.State.LoadedFavTracks -> {
                    binding.favTrackList.visibility = View.VISIBLE
                    unifiedTrackAdapter.updateTracks(state.favTracks)
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.emptyFav.visibility = View.GONE
                }
            }
        }
    }
}