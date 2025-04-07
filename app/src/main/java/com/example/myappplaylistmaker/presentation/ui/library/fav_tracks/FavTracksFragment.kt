package com.example.myappplaylistmaker.presentation.ui.library.fav_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentFavtrackBinding
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.ui.search.UnifiedTrackAdapter
import com.example.myappplaylistmaker.presentation.utils.debounce
import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavTracksFragment : Fragment() {

    private var _binding: FragmentFavtrackBinding? = null
    private val binding get() = _binding!!
    private val favTracksViewModel by viewModel<FavTracksViewModel>()
    private lateinit var unifiedTrackAdapter: UnifiedTrackAdapter
    private var isDebounceEnabled = true
    private lateinit var debouncedClick: (Track) -> Unit

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

        unifiedTrackAdapter = UnifiedTrackAdapter (
            tracks = mutableListOf(),
            onTrackClick = { track ->
                openTrack(track)
                onDebouncedClick()
            }
        )


        binding.favTrackList.layoutManager = LinearLayoutManager(requireContext())
        binding.favTrackList.adapter = unifiedTrackAdapter
        binding.progressBar.isVisible = true
        binding.favTrackList.isVisible = false
        binding.emptyFav.isVisible = false
        binding.noSongPlaceholder.isVisible = false

        observeState()
        favTracksViewModel.getFavTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openTrack(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("TRACK_DATA", track)
        }
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_mediaPlayerFragment2,
            bundle
        )
    }

    private fun observeState() {

        favTracksViewModel.favTracks.observe(viewLifecycleOwner) { tracks ->

            viewLifecycleOwner.lifecycleScope.launch {

                delay(500L)
                unifiedTrackAdapter.updateTracks(tracks)
                binding.favTrackList.isVisible = tracks.isNotEmpty()
                binding.noSongPlaceholder.isVisible = tracks.isEmpty()
                binding.emptyFav.isVisible = tracks.isEmpty()
                binding.progressBar.isGone = true
            }
        }
    }

    private fun onDebouncedClick() {
        debouncedClick = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { isDebounceEnabled = true
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}
