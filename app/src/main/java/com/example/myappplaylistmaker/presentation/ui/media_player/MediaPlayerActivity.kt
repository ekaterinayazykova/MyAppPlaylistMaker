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
    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor


//    private val handler = Handler(Looper.getMainLooper())
//    private var songUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        mediaPlayerInteractor = Creator.createPlayer()

        val track = intent.getParcelableExtra<Track>("track")
        track?.let {
            fetchTrackData(it)
            mediaPlayerViewModel.prepare(track)
        }

        binding.arrow.setOnClickListener {
            mediaPlayerInteractor.stop()
            onBackPressed()
        }

        binding.buttonAdd.setOnClickListener{
            TODO()
        }
        binding.buttonLike.setOnClickListener{
            TODO()
        }

        binding.buttonPlay.setOnClickListener {
//            PlayerState.PREPARED
//            PlayerState.PAUSED
//            binding.buttonPlayProgressBar.visibility = View.VISIBLE
            binding.buttonPlay.isEnabled = false

            mediaPlayerViewModel.playback()
            mediaPlayerViewModel.startCountdown()

            Handler(Looper.getMainLooper()).postDelayed({
                updatePlayButton()
//                binding.buttonPlayProgressBar.visibility = View.GONE
                binding.buttonPlay.isEnabled = true
            }, 300)
        }

        mediaPlayerViewModel.durationTime.observe(this) { time ->
            binding.trackDuration.text = time
//            updatePlayButton()
        }

        mediaPlayerViewModel.choosePlayerOption.observe(this) { option ->
            option?.let {
                when (it) {
                    MediaPlayerViewModel.Option.PLAY -> {
                        mediaPlayerViewModel.startPlayer()
                        updatePlayButton()
                    }
                    MediaPlayerViewModel.Option.PREPARE -> {
                        track?.let { mediaPlayerViewModel.preparePlayer(it) }
                    }
                    MediaPlayerViewModel.Option.PAUSE -> {
                        mediaPlayerViewModel.pausePlayer()
                        updatePlayButton()
                    }
                    MediaPlayerViewModel.Option.PLAYBACK -> {
                        mediaPlayerViewModel.playbackControl()
                    }
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.pause()
    }

    private fun fetchTrackData(track: Track) {
        var songUrl = track.previewUrl
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackCountryInfo.text = track.country
        binding.trackAlbumInfo.text = track.collectionName
        binding.trackYearInfo.text = track.releaseDate
        binding.trackGenreInfo.text = track.genre
        binding.trackDurationTimeInfo.text = formatTrackTime(track.trackTimeMillis)

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
//            binding.buttonPlayProgressBar.visibility = View.GONE
        } else {
            binding.buttonPlay.setImageResource(R.drawable.button_play)
//            binding.buttonPlayProgressBar.visibility = View.GONE
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
