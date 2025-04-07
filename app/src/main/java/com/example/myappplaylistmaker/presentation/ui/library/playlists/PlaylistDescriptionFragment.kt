package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentPlaylistDescriptionBinding
import com.example.myappplaylistmaker.domain.entity.DomainPlaylistWithTracks
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.ui.search.UnifiedTrackAdapter
import com.example.myappplaylistmaker.presentation.utils.Utils
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistDescriptionViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlaylistDescriptionFragment: Fragment() {

    private var _binding: FragmentPlaylistDescriptionBinding? = null
    private val binding get() = _binding!!
    private val playlistDescriptionViewModel by viewModel<PlaylistDescriptionViewModel>()
    private lateinit var overlay: View
    private lateinit var bottomSheetArray: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetMenu: BottomSheetBehavior<ConstraintLayout>
    private lateinit var unifiedTrackAdapter: UnifiedTrackAdapter
    private val playlistId: Int by lazy {arguments?.getInt("PLAYLIST_ID") ?: throw IllegalArgumentException("id is null")}
    private var playlistWithTracks: DomainPlaylistWithTracks? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistDescriptionViewModel.getPlaylistById(playlistId)
        setupAdapter()
        observeState()
        setupBottomSheet()
        setupBinding()

        binding.bottomSheetPlaylistArray.progressBar.isVisible = true
        binding.bottomSheetPlaylistArray.emptyTracksPlaceholder.isVisible = false
        overlay = binding.overlay

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        unifiedTrackAdapter = UnifiedTrackAdapter(
            tracks = (mutableListOf()),
            onTrackClick = { track -> openTrack(track) },
            onLongTrackClick = {track ->
                confirmDialog(
                    message = getString(R.string.deleteConfirmTitle),
                    onConfirm = {
                        playlistDescriptionViewModel.removeTrackFromPlaylist(track.trackId, playlistId)
                    }
                )}
        )
    }

    private fun setupBinding() {

        binding.overlay.setOnClickListener {
            if (bottomSheetMenu.state != BottomSheetBehavior.STATE_HIDDEN ) {
                bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.bottomSheetPlaylistArray.playlistRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unifiedTrackAdapter
        }

        binding.ivSharePlaylist.setOnClickListener{
            playlistWithTracks?.let { playlist ->
                sharePlaylist(playlist)
            }
        }

        binding.arrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.ivPlaylistMenu.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.bottomSheetPlaylistMenu.sharePlaylist.setOnClickListener{
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            playlistWithTracks?.let { playlist ->
                sharePlaylist(playlist)
            }
        }

        binding.bottomSheetPlaylistMenu.editPlaylist.setOnClickListener {
            editPlaylist(playlistId)
        }

        binding.bottomSheetPlaylistMenu.deletePlaylist.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            playlistWithTracks?.let { playlist ->
                confirmDialog(
                    message = getString(R.string.deletePlaylistConfirm, playlist.playlist.playlistName),
                    playlistWithTracks = playlist,
                    onConfirm = { playlistId, trackIds ->
                        playlistDescriptionViewModel.deletePlaylist(playlistId, trackIds)
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }

    private fun editPlaylist(playlistId: Int) {
        val bundle = Bundle().apply {
            putInt("PLAYLIST_ID", playlistId)
        }
        findNavController().navigate(
            R.id.action_playlistDescriptionFragment_to_editPlaylistFragment,
            bundle
        )
    }

    private fun observeState() {
        playlistDescriptionViewModel.contentState.observe(viewLifecycleOwner) { content ->
            when (content) {
                is PlaylistDescriptionViewModel.PlaylistContent.Content -> {
                    playlistWithTracks = content.listTracks
                    binding.bottomSheetPlaylistArray.emptyTracksPlaceholder.isVisible = false
                    unifiedTrackAdapter.updateTracks(content.listTracks.tracks)
                    fetchPlaylistData(content.listTracks)
                    binding.bottomSheetPlaylistArray.progressBar.isVisible = false
                }
                is PlaylistDescriptionViewModel.PlaylistContent.NoContent -> {
                    playlistWithTracks = content.playlistInfo
                    binding.bottomSheetPlaylistArray.emptyTracksPlaceholder.isVisible = true
                    binding.bottomSheetPlaylistArray.playlistRecycler.isVisible = false
                    fetchPlaylistData(content.playlistInfo)
                    binding.bottomSheetPlaylistArray.progressBar.isVisible = false
                }
            }
        }
    }

    private fun sharePlaylist(playlistWithTracks: DomainPlaylistWithTracks) {
        if (playlistWithTracks.tracks.size >= 1) {
            val shareIntent = playlistDescriptionViewModel.sharePlaylist(
                shareMessage = buildString {
                    append("${playlistWithTracks.playlist.playlistName}\n")
                    append("${playlistWithTracks.playlist.playlistDescription}\n")
                    append("${playlistWithTracks.tracks.size} треков\n\n")

                    playlistWithTracks.tracks.forEachIndexed { index, track ->
                        append("${index + 1}.${track.artistName} - ${track.trackName} (${formatDuration(track.trackTimeMillis)})")
                    }
                }
            )
            val chooser = Intent.createChooser(shareIntent, "")
            startActivity(chooser)
        } else {
            showSnackBar(message = getString(R.string.no_tracks_to_share))
        }
    }

    private fun fetchPlaylistData(playlistWithTracks: DomainPlaylistWithTracks) {
        val playlist = playlistWithTracks.playlist
        binding.tvPlaylistName.text = playlist.playlistName
        binding.tvPlaylistDescription.text = playlist.playlistDescription
        val duration = getPlaylistDuration(playlistWithTracks)
        val durationWord = getProperWord(duration, "минута", "минуты", "минут" )
        binding.tvPlaylistDuration.text = "$duration $durationWord"
        val count = playlistWithTracks.tracks.size
        val trackWord = getProperWord(count, "трек", "трека", "треков")
        binding.tvPlaylistSize.text = "$count $trackWord"
        unifiedTrackAdapter.updateTracks(playlistWithTracks.tracks)
        binding.bottomSheetPlaylistMenu.playlistItem.playlistName.text = playlist.playlistName
        binding.bottomSheetPlaylistMenu.playlistItem.tracksAmount.text = "$count $trackWord"
        val imageUrl = if (playlist.imagePath.isNotEmpty()) playlist.imagePath else R.drawable.album_placeholder
        val radius = Utils.dpToPx(8f, requireContext())
        val transformation = MultiTransformation(CenterCrop(), RoundedCorners(radius))
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions().transform(transformation))
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(binding.ivPlaylistCover)
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions().transform(transformation))
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(binding.bottomSheetPlaylistMenu.playlistItem.playlistCover)
    }

    fun getProperWord(count: Int, form1: String, form2: String, form3: String): String {
        val lastTwoDigits = count % 100
        val lastDigit = count % 10
        if (lastTwoDigits in 11..14) {
            return form3
        }
        return when (lastDigit) {
            1 -> form1
            2, 3, 4 -> form2
            else -> form3
        }
    }

    private fun setupBottomSheet() {
        bottomSheetMenu = BottomSheetBehavior.from(binding.bottomSheetPlaylistMenu.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = slideOffset
                }
            })
        }
        bottomSheetArray = BottomSheetBehavior.from(binding.bottomSheetPlaylistArray.root).apply {
            peekHeight = Utils.dpToPx(268f, requireContext())
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            expandedOffset = 0
            isFitToContents = false
            skipCollapsed = false
            halfExpandedRatio = 0.9999f
        }
    }

    private fun getPlaylistDuration(playlistWithTracks: DomainPlaylistWithTracks): Int {
        val durationSum = playlistWithTracks.tracks.sumOf { it.trackTimeMillis }
        val durationSumMinutes = TimeUnit.MILLISECONDS.toMinutes(durationSum)
        return durationSumMinutes.toInt()
    }

    private fun formatDuration(millis: Long): String {
        val minutes = millis / 60000
        val seconds = (millis % 60000) / 1000
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun openTrack(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("TRACK_DATA", track)
        }
        findNavController().navigate(
            R.id.action_playlistDescriptionFragment_to_mediaPlayerFragment,
            bundle
        )
    }

    private fun confirmDialog(message: String,
                              playlistWithTracks: DomainPlaylistWithTracks,
                              onConfirm: (Int, List<String>) -> Unit) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(message)
                .setPositiveButton(R.string.yes) { dialog, witch ->
                    val playlistId = playlistWithTracks.playlist.playlistId
                    val trackIds = playlistWithTracks.tracks.map { it.trackId }
                    onConfirm(playlistId,trackIds)
                }
                .setNegativeButton(R.string.no) { dialog, witch -> dialog.dismiss()
                }
                .show()
    }

    private fun confirmDialog(message: String,
                              onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(message)
            .setPositiveButton(R.string.yes) { dialog, witch ->  onConfirm()
            }
            .setNegativeButton(R.string.no) { dialog, witch -> dialog.dismiss()
            }
            .show()
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
}

