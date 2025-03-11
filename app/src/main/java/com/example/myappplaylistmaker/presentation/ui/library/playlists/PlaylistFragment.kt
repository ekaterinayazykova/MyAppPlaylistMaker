package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentPlaylistBinding
import com.example.myappplaylistmaker.domain.entity.Playlist
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.utils.debounce
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val playlistViewModel by viewModel<PlaylistViewModel>()
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var debouncedClick: (Track) -> Unit
    private var isDebounceEnabled = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistAdapter = PlaylistAdapter(mutableListOf()) { playlist ->
            openPlaylist(playlist)
            debouncedClick()
        }

        binding.playlistsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecycler.adapter = playlistAdapter
        binding.newPlaylist.isVisible = true
        binding.progressBar.isVisible = true
        binding.playlistsRecycler.isVisible = false
        binding.emptyFavFirst.isVisible = false
        binding.emptyFavSecond.isVisible = false
        binding.noSongPlaceholder.isVisible = false
        observeState()
        playlistViewModel.getPlaylist()

        binding.newPlaylist.setOnClickListener{
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeState() {
        playlistViewModel.playlistState.observe(viewLifecycleOwner) { playlists ->
            viewLifecycleOwner.lifecycleScope.launch {

                delay(500L)
                playlistAdapter.updatePlaylist(playlists)
                binding.playlistsRecycler.isVisible = playlists.isNotEmpty()
                binding.noSongPlaceholder.isVisible = playlists.isEmpty()
                binding.emptyFavFirst.isVisible = playlists.isEmpty()
                binding.emptyFavSecond.isVisible = playlists.isEmpty()
                binding.progressBar.isGone = true
            }
        }
    }

    private fun debouncedClick() {
        debouncedClick = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { isDebounceEnabled = true
        }
    }

    private fun openPlaylist(playlist: Playlist) {

    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

}
