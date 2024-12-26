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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import com.example.myappplaylistmaker.domain.entity.PlayerState
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.presentation.utils.Utils

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var artworkImageView: ImageView
    private lateinit var trackTimeTextView: TextView
    private lateinit var collectionNameTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var genreNameTextView: TextView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var addToFavoritesButton: ImageView
    private lateinit var currentTrackTime: TextView
    private lateinit var backButton: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor
    private val handler = Handler(Looper.getMainLooper())
    private var songUrl: String = ""
    private var playerState = PlayerState.DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)

        trackNameTextView = findViewById(R.id.track_name)
        artistNameTextView = findViewById(R.id.artist_name)
        artworkImageView = findViewById(R.id.track_cover)
        trackTimeTextView = findViewById(R.id.track_duration_time_info)
        collectionNameTextView = findViewById(R.id.track_album_info)
        releaseDateTextView = findViewById(R.id.track_year_info)
        countryTextView = findViewById(R.id.track_country_info)
        genreNameTextView = findViewById(R.id.track_genre)
        addToPlaylistButton = findViewById(R.id.button_add)
        playButton = findViewById(R.id.button_play)
        progressBar = findViewById(R.id.button_play_progress_bar)
        addToFavoritesButton = findViewById(R.id.button_like)
        currentTrackTime = findViewById(R.id.track_duration)
        backButton = findViewById(R.id.arrow)

        mediaPlayerInteractor = Creator.createPlayer()

        val track = intent.getParcelableExtra<Track>("track")
        track?.let {
            fetchTrackData(it)
            preparePlayer(track)
        }

        backButton.setOnClickListener {
            mediaPlayerInteractor.stop()
            onBackPressed()
        }

        playButton.setOnClickListener {
            PlayerState.PREPARED
            PlayerState.PAUSED
            playbackControl()
            startCountdown()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun startCountdown() {
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayerInteractor.isPlaying()) {
                    val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    currentTrackTime.text = formattedTime
                    handler.postDelayed(this, 1000)

                } else {
                    mediaPlayerInteractor.pause()
                    playButton.setImageResource(R.drawable.button_play)
                    handler.removeCallbacks(this)
                }
            }
        })
    }

    private fun fetchTrackData(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = formatTrackTime(track.trackTimeMillis)
        countryTextView.text = track.country
        collectionNameTextView.text = track.collectionName
        releaseDateTextView.text = track.releaseDate
        songUrl = track.previewUrl

        if (!track.collectionName.isNullOrEmpty()) {
            collectionNameTextView.text = track.collectionName
            collectionNameTextView.visibility = View.VISIBLE
        } else {
            collectionNameTextView.visibility = View.GONE
        }

        val releaseData = track.releaseDate
        if (releaseData != null) {
            if (releaseData.length >= 4) {
                val year = releaseData.substring(0, 4)
                releaseDateTextView.text = year
            }
        }

        var artworkUrl = track.artworkUrl100
        artworkUrl = artworkUrl.replaceAfterLast('/', "512x512bb.jpg")

        if (NetworkClass.isNetworkAvailable(this)) {
            Glide.with(this)
                .load(artworkUrl)
                .fitCenter()
                .placeholder(R.drawable.icon_placeholder)
                .centerCrop()
                .transform(RoundedCorners(Utils.dpToPx(8f, this)))
                .into(artworkImageView)
        } else {
            artworkImageView.setImageResource(R.drawable.icon_placeholder)
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

    private fun preparePlayer(track: Track) {
        if (!songUrl.isNullOrEmpty()) {
            mediaPlayerInteractor.execute(track)
            playerState = PlayerState.PREPARED
        }
    }

        private fun startPlayer() {
            mediaPlayerInteractor.play()
            startCountdown()
            playButton.setImageResource(R.drawable.button_pause)
            playerState = PlayerState.PLAYING
        }

        private fun pausePlayer() {
            if (mediaPlayerInteractor.isPlaying()) {
                mediaPlayerInteractor.pause()
                playButton.setImageResource(R.drawable.button_play)
                playerState = PlayerState.PAUSED
            }
        }

        private fun playbackControl() {
            Log.e("PlayerState", "${playerState}")
            when (playerState) {
                PlayerState.PLAYING -> pausePlayer()
                PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
                PlayerState.DEFAULT -> {
                    Log.e("PlaybackControl", "PlayerErrorState")
                }
            }
        }
    }
