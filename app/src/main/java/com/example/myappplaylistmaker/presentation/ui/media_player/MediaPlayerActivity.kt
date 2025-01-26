package com.example.myappplaylistmaker.presentation.ui.media_player

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.ActivityTrackBinding
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.utils.Utils
import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding
    private val mediaPlayerViewModel by viewModel<MediaPlayerViewModel>()
    private lateinit var screenReceiver: ScreenReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        screenReceiver = ScreenReceiver()


        val track = intent.getSerializableExtra(TRACK_DATA) as? Track
        Log.d("MediaPlayerActivity", "Track data received: $track")
        track?.let {
            mediaPlayerViewModel.setTrack(it)
            fetchTrackData(it)
        }

        mediaPlayerViewModel.state.observe(this) { state ->
            when (state) {
                is MediaPlayerViewModel.State.LOADING -> {
                    binding.buttonPlay.visibility = View.INVISIBLE
                    binding.buttonPlayProgressBar.visibility = View.VISIBLE
                }
                is MediaPlayerViewModel.State.PREPARED -> {
                    binding.buttonPlay.visibility = View.VISIBLE
                    binding.buttonPlayProgressBar.visibility = View.GONE
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
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
                }
            }
        }

        binding.arrow.setOnClickListener {
            mediaPlayerViewModel.stopPlayer()
            finish()
        }

        binding.buttonAdd.setOnClickListener {
//            TODO()
        }
        binding.buttonLike.setOnClickListener {
//            TODO()
        }

        binding.buttonPlay.setOnClickListener {
            mediaPlayerViewModel.playbackControl()
            updatePlayButton()
        }
        updatePlayButton()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerViewModel.cleanup()
        mediaPlayerViewModel.stopPlayer()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(screenReceiver)
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
            if (NetworkClass.isNetworkAvailable(this)) {
                Glide.with(this)
                    .load(modifiedArtworkUrl)
                    .fitCenter()
                    .placeholder(R.drawable.icon_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(Utils.dpToPx(8f, this)))
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

    private fun updatePlayButton() {
        if (mediaPlayerViewModel.isPlaying()) {
            binding.buttonPlay.setImageResource(R.drawable.button_pause)
        } else {
            binding.buttonPlay.setImageResource(R.drawable.button_play)
        }
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    @Deprecated("This method use back button.")
    override fun onBackPressed() {
        mediaPlayerViewModel.stopPlayer()
        super.onBackPressed()
        finish()
    }

    companion object {
        const val TRACK_DATA = "TRACK_DATA"
    }
}