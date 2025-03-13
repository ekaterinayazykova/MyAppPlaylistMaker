package com.example.myappplaylistmaker.presentation.ui.media_player

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentMediaPlayerBinding
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import com.example.myappplaylistmaker.presentation.utils.Utils
import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaPlayerFragment : Fragment() {

    private val mediaPlayerViewModel by viewModel<MediaPlayerViewModel>()
    private var _binding: FragmentMediaPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var screenReceiver: ScreenReceiver
    private lateinit var overlay: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetAdapter: BottomSheetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenReceiver = ScreenReceiver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.bottomSheetNewPlaylist.rvPlaylists
        overlay = binding.overlay

        setupBottomSheet()
        observeState()
        mediaPlayerViewModel.getPlaylist()
        setupOnPlaylistClick()

        binding.bottomSheetNewPlaylist.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetNewPlaylist.rvPlaylists.adapter = bottomSheetAdapter

        val track = arguments?.getSerializable(TRACK_DATA) as? Track
        track?.let {
            mediaPlayerViewModel.setTrack(it)
            mediaPlayerViewModel.checkFavorite(it)
            fetchTrackData(it)
        }

        binding.bottomSheetNewPlaylist.newPlaylist.setOnClickListener {
            openCreatePlaylistFragment()
        }

        binding.arrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.buttonLike.setOnClickListener {
            mediaPlayerViewModel.onFavoriteClicked()
        }

        binding.buttonPlay.setOnClickListener {
            mediaPlayerViewModel.playbackControl()
            updatePlayButton()
        }
        updatePlayButton()

        mediaPlayerViewModel.currentTrack.observe(viewLifecycleOwner) { currentTrack ->
            if (currentTrack.isFavorite) {
                binding.buttonLike.setImageResource(R.drawable.button_fav)
            } else {
                binding.buttonLike.setImageResource(R.drawable.button_like)
            }
        }

        mediaPlayerViewModel.trackInPlaylist.observe(viewLifecycleOwner) { pair ->
            val (isInPlaylist, name) = pair
            if (isInPlaylist) {
                showSnackBar(getString(R.string.trackNotAdded, name))
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showSnackBar(getString(R.string.trackAdded, name))
            }
        }

        mediaPlayerViewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d("MediaPlayerUI", "Observed state: $state")
            when (state) {
                is MediaPlayerViewModel.State.LOADING -> {
                    binding.buttonPlay.isVisible = false
                    binding.buttonPlayProgressBar.isVisible = true
                }
                is MediaPlayerViewModel.State.PREPARED -> {
                    binding.buttonPlay.visibility = View.VISIBLE
                    binding.buttonPlayProgressBar.visibility = View.GONE
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
                    binding.trackDuration.text = state.progress
                }
                is MediaPlayerViewModel.State.PLAYING -> {
                    binding.buttonPlay.visibility = View.VISIBLE
                    binding.buttonPlayProgressBar.visibility = View.GONE
                    binding.buttonPlay.setImageResource(R.drawable.button_pause)
                    binding.trackDuration.text = state.currentTime
                }
                is MediaPlayerViewModel.State.PAUSED -> {
                    binding.buttonPlay.visibility = View.VISIBLE
                    binding.buttonPlayProgressBar.visibility = View.GONE
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
                    binding.trackDuration.text = state.currentTime
                }
                is MediaPlayerViewModel.State.STOPPED -> {
                    binding.buttonPlay.visibility = View.VISIBLE
                    binding.buttonPlayProgressBar.visibility = View.GONE
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
                    binding.trackDuration.text = state.progress
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(screenReceiver)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayerViewModel.stopPlayer()
        _binding = null
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetNewPlaylist.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.overlay.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = ((slideOffset + 1) / 2).coerceIn(0f, 1f)
                binding.overlay.alpha = alpha
            }
        })
    }

    private fun observeState() {
        mediaPlayerViewModel.playlistState.observe(viewLifecycleOwner) { playlists ->
            viewLifecycleOwner.lifecycleScope.launch {
                bottomSheetAdapter.updatePlaylist(playlists)
                recyclerView.isVisible = playlists.isNotEmpty()
            }
        }
    }

    private fun openCreatePlaylistFragment() {
        findNavController().navigate(R.id.action_mediaPlayerFragment_to_createPlaylistFragment)
    }

    private fun updatePlayButton() {
        if (mediaPlayerViewModel.isPlaying()) {
            binding.buttonPlay.setImageResource(R.drawable.button_pause)
        } else {
            binding.buttonPlay.setImageResource(R.drawable.button_play)
        }
    }

    private fun fetchTrackData(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackCountryInfo.text = track.country
        binding.trackAlbumInfo.text = track.collectionName
        binding.trackYearInfo.text = track.releaseDate
        binding.trackGenreInfo.text = track.genre
        binding.trackDurationTimeInfo.text = formatTrackTime(track.trackTimeMillis ?: 0)

        updateAlbumInfoVisibility(track.collectionName)
        updateReleaseYear(track.releaseDate)
        loadArtwork(track.artworkUrl100)
    }

    private fun loadArtwork(artworkUrl: String?) {
        artworkUrl?.let {
            val modifiedArtworkUrl = it.replaceAfterLast('/', "512x512bb.jpg")
            if (NetworkClass.isNetworkAvailable(requireContext())) {
                Glide.with(requireContext())
                    .load(modifiedArtworkUrl)
                    .placeholder(R.drawable.icon_placeholder)
                    .fitCenter()
                    .transform(RoundedCorners(Utils.dpToPx(8f, requireContext())))
                    .into(binding.trackCover)
            } else {
                binding.trackCover.setImageResource(R.drawable.icon_placeholder)
            }
        } ?: run {
            binding.trackCover.setImageResource(R.drawable.icon_placeholder)
        }
    }

    private fun updateAlbumInfoVisibility(collectionName: String?) {
        binding.trackAlbumInfo.visibility = if (!collectionName.isNullOrEmpty()) {
            binding.trackAlbumInfo.text = collectionName
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateReleaseYear(releaseDate: String?) {
        releaseDate?.let {
            if (it.length >= 4) {
                binding.trackYearInfo.text = it.substring(0, 4)
            }
        }
    }

    private fun setupOnPlaylistClick() {
        bottomSheetAdapter = BottomSheetAdapter(mutableListOf()) { playlist ->
            val track = mediaPlayerViewModel.currentTrack.value
            if (track != null) {
                mediaPlayerViewModel.checkIsTrackInPlaylist(track.trackId,playlist.playlistId, playlist.playlistName)
                mediaPlayerViewModel.addTrackToPlaylist(
                    track = track,
                    playlistId = playlist.playlistId
                )
            }
        }
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun showSnackBar(message: String) {
        val snackbar =
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setTypeface(typeface)
        snackbar.show()
    }

    companion object {
        const val TRACK_DATA = "TRACK_DATA"
    }
}
