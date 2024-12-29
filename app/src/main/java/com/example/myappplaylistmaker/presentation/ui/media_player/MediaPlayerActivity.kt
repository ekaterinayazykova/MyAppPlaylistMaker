package com.example.myappplaylistmaker.presentation.ui.media_player

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.databinding.ActivityTrackBinding
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import com.example.myappplaylistmaker.domain.entity.PlayerState
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.presentation.utils.Utils
import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModel
import com.example.myappplaylistmaker.presentation.view_models.media_player.MediaPlayerViewModelFactory

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding
    private val mediaPlayerViewModel: MediaPlayerViewModel by viewModels { MediaPlayerViewModelFactory(Creator.createPlayer()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val track = intent.getParcelableExtra<Track>("track")

        track?.let {
            fetchTrackData(it)
            mediaPlayerViewModel.updatePlayerOption(MediaPlayerViewModel.Option.PREPARE)
        }

        binding.arrow.setOnClickListener {
            mediaPlayerViewModel.stop()
            onBackPressed()
        }

        binding.buttonAdd.setOnClickListener{
//            TODO()
        }
        binding.buttonLike.setOnClickListener{
//            TODO()
        }

        binding.buttonPlay.setOnClickListener {
            mediaPlayerViewModel.updatePlayerOption(MediaPlayerViewModel.Option.PLAYBACK)
        }

        mediaPlayerViewModel.durationTime.observe(this) { time ->
            binding.trackDuration.text = time
        }

        mediaPlayerViewModel.choosePlayerOption.observe(this) { option ->
            Log.e("TAG", "option: $option")
            option?.let {
                when (it) {
                    MediaPlayerViewModel.Option.PREPARE -> {
                        updatePlayButton()
                        binding.buttonPlay.visibility = View.VISIBLE
                        binding.buttonPlayProgressBar.visibility = View.INVISIBLE
                        track?.let { mediaPlayerViewModel.preparePlayer(it) }
                    }
                    MediaPlayerViewModel.Option.PLAYBACK -> {
                        updatePlayButton()
                        mediaPlayerViewModel.playbackControl()
                    }
                    MediaPlayerViewModel.Option.LOADING -> {
                        binding.buttonPlay.visibility = View.INVISIBLE
                        binding.buttonPlayProgressBar.visibility = View.VISIBLE
                    }
                    MediaPlayerViewModel.Option.PLAYING,
                    MediaPlayerViewModel.Option.PAUSE -> updatePlayButton()
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.pausePlayer()
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
        super.onBackPressed()
        finish()
    }
}
