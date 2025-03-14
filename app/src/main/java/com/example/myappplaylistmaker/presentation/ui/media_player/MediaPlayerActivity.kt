//package com.example.myappplaylistmaker.presentation.ui.media_player
//
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.core.view.isGone
//import androidx.core.view.isVisible
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.Navigation
//import androidx.navigation.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.example.myappplaylistmaker.R
//import com.example.myappplaylistmaker.databinding.ActivityTrackBinding
//import com.example.myappplaylistmaker.domain.entity.Track
//import com.example.myappplaylistmaker.presentation.ui.library.playlists.CreatePlaylistFragment
//import com.example.myappplaylistmaker.presentation.ui.library.playlists.PlaylistAdapter
//import com.example.myappplaylistmaker.presentation.ui.root.RootActivity
//import com.example.myappplaylistmaker.presentation.utils.NetworkClass
//import com.example.myappplaylistmaker.presentation.utils.Utils
//import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModel
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class MediaPlayerActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityTrackBinding
//    private val mediaPlayerViewModel by viewModel<MediaPlayerViewModel>()
//    private lateinit var screenReceiver: ScreenReceiver
//    private lateinit var overlay: View
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
//    private lateinit var bottomSheetAdapter: BottomSheetAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityTrackBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        screenReceiver = ScreenReceiver()
//        recyclerView = binding.bottomSheetNewPlaylist.rvPlaylists
//        overlay = binding.overlay
//
//        setupBottomSheet()
//        observeState()
//        mediaPlayerViewModel.getPlaylist()
//
//        bottomSheetAdapter = BottomSheetAdapter(mutableListOf()) { playlist ->
//
//        }
//
//        binding.bottomSheetNewPlaylist.rvPlaylists.layoutManager = LinearLayoutManager(this)
//        binding.bottomSheetNewPlaylist.rvPlaylists.adapter = bottomSheetAdapter
//
//        val track = intent.getSerializableExtra(TRACK_DATA) as? Track
//        track?.let {
//            mediaPlayerViewModel.setTrack(it)
//            mediaPlayerViewModel.checkFavorite(it)
//            fetchTrackData(it)
//        }
//
//        mediaPlayerViewModel.currentTrack.observe(this) { currentTrack ->
//            if (currentTrack.isFavorite) {
//                binding.buttonLike.setImageResource(R.drawable.button_fav)
//            } else binding.buttonLike.setImageResource(R.drawable.button_like)
//        }
//
//        mediaPlayerViewModel.state.observe(this) { state ->
//            when (state) {
//                is MediaPlayerViewModel.State.LOADING -> {
//                    binding.buttonPlay.visibility = View.INVISIBLE
//                    binding.buttonPlayProgressBar.visibility = View.VISIBLE
//                }
//                is MediaPlayerViewModel.State.PREPARED -> {
//                    binding.buttonPlay.visibility = View.VISIBLE
//                    binding.buttonPlayProgressBar.visibility = View.GONE
//                    binding.buttonPlay.setImageResource(R.drawable.button_play)
//                }
//
//                is MediaPlayerViewModel.State.PLAYING -> {
//                    binding.buttonPlay.visibility = View.VISIBLE
//                    binding.buttonPlayProgressBar.visibility = View.GONE
//                    binding.buttonPlay.setImageResource(R.drawable.button_pause)
//                    binding.trackDuration.text = state.currentTime
//                }
//
//                is MediaPlayerViewModel.State.PAUSED -> {
//                    binding.buttonPlay.visibility = View.VISIBLE
//                    binding.buttonPlayProgressBar.visibility = View.GONE
//                    binding.buttonPlay.setImageResource(R.drawable.button_play)
//                    binding.trackDuration.text = state.currentTime
//                }
//            }
//        }
//
//        binding.bottomSheetNewPlaylist.newPlaylist.setOnClickListener{
//            openCreatePlaylistFragment()
//        }
//
//        binding.arrow.setOnClickListener {
//            mediaPlayerViewModel.stopPlayer()
//            finish()
//        }
//
//        binding.buttonAdd.setOnClickListener {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }
//        binding.buttonLike.setOnClickListener {
//            mediaPlayerViewModel.onFavoriteClicked()
//        }
//
//        binding.buttonPlay.setOnClickListener {
//            mediaPlayerViewModel.playbackControl()
//            updatePlayButton()
//        }
//        updatePlayButton()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mediaPlayerViewModel.pausePlayer()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayerViewModel.stopPlayer()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
//    }
//
//    override fun onStop() {
//        super.onStop()
//        unregisterReceiver(screenReceiver)
//    }
//
//    private fun fetchTrackData(track: Track) {
//        binding.trackName.text = track.trackName
//        binding.artistName.text = track.artistName
//        binding.trackCountryInfo.text = track.country
//        binding.trackAlbumInfo.text = track.collectionName
//        binding.trackYearInfo.text = track.releaseDate
//        binding.trackGenreInfo.text = track.genre
//        binding.trackDurationTimeInfo.text = formatTrackTime(track.trackTimeMillis ?: 0)
//
//        updateAlbumInfoVisibility(track.collectionName)
//        updateReleaseYear(track.releaseDate)
//        loadArtwork(track.artworkUrl100)
//    }
//    private fun loadArtwork(artworkUrl: String?) {
//        artworkUrl?.let {
//            val modifiedArtworkUrl = it.replaceAfterLast('/', "512x512bb.jpg")
//            if (NetworkClass.isNetworkAvailable(this)) {
//                Glide.with(this)
//                    .load(modifiedArtworkUrl)
//                    .fitCenter()
//                    .placeholder(R.drawable.icon_placeholder)
//                    .centerCrop()
//                    .transform(RoundedCorners(Utils.dpToPx(8f, this)))
//                    .into(binding.trackCover)
//            } else {
//                binding.trackCover.setImageResource(R.drawable.icon_placeholder)
//            }
//        } ?: run {
//            binding.trackCover.setImageResource(R.drawable.icon_placeholder)
//        }
//    }
//
//    private fun updateAlbumInfoVisibility(collectionName: String?) {
//        binding.trackAlbumInfo.visibility = if (!collectionName.isNullOrEmpty()) {
//            binding.trackAlbumInfo.text = collectionName
//            View.VISIBLE
//        } else {
//            View.GONE
//        }
//    }
//
//    private fun updateReleaseYear(releaseDate: String?) {
//        releaseDate?.let {
//            if (it.length >= 4) {
//                binding.trackYearInfo.text = it.substring(0, 4)
//            }
//        }
//    }
//
//    private fun updatePlayButton() {
//        if (mediaPlayerViewModel.isPlaying()) {
//            binding.buttonPlay.setImageResource(R.drawable.button_pause)
//        } else {
//            binding.buttonPlay.setImageResource(R.drawable.button_play)
//        }
//    }
//
//
//    private fun formatTrackTime(trackTimeMillis: Long): String {
//        val minutes = (trackTimeMillis / 1000) / 60
//        val seconds = (trackTimeMillis / 1000) % 60
//        return String.format("%02d:%02d", minutes, seconds)
//    }
//
//    private fun setupBottomSheet() {
//        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetNewPlaylist.root).apply {
//            state = BottomSheetBehavior.STATE_HIDDEN
//        }
//
//        binding.overlay.setOnClickListener{
//            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//            }
//        }
//
//        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> {
//                        overlay.visibility = View.GONE
//                    }
//                    else -> {
//                        overlay.visibility = View.VISIBLE
//                    }
//                }
//            }
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                val alpha = ((slideOffset + 1) / 2).coerceIn(0f, 1f)
//                binding.overlay.alpha = alpha
//            }
//        })
//    }
//
//    private fun observeState() {
//        mediaPlayerViewModel.playlistState.observe(this) { playlists ->
//            lifecycleScope.launch {
//                bottomSheetAdapter.updatePlaylist(playlists)
//                recyclerView.isVisible = playlists.isNotEmpty()
////                binding.progressBar.isGone = true
//            }
//        }
//    }
//
//    private fun openCreatePlaylistFragment(){
//        binding.fragmentContainer.isVisible = true
//        val fragment = CreatePlaylistFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, fragment, "CreatePlaylistFragment")
//            .addToBackStack(null)
//            .commit()
//    }
//
//    @Deprecated("This method use back button.")
//    override fun onBackPressed() {
//        mediaPlayerViewModel.stopPlayer()
//        super.onBackPressed()
//        finish()
//    }
//
//    companion object {
//        const val TRACK_DATA = "TRACK_DATA"
//    }
//}