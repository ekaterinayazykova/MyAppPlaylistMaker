package com.example.myappplaylistmaker

import android.annotation.SuppressLint
import android.app.usage.NetworkStats.Bucket.STATE_DEFAULT
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
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
import androidx.room.util.query
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackActivity : AppCompatActivity() {

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


    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private val screenReceiver: ScreenReceiver by lazy { ScreenReceiver() }
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    private var currentPositionMillis = 0
    private var playerIsPrepared: Boolean = false

    private var songUrl: String = ""

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

        val track = intent.getParcelableExtra<Track>("track")
        track?.let {
            fetchTrackData(it)
            preparePlayer()
        }

        screenReceiver.playbackCallback = {
            playerState = STATE_PLAYING
            playbackControl()
        }

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)

        backButton.setOnClickListener {
            onBackPressed()
        }

        playButton.setOnClickListener {
            onPlayPauseButtonClick()
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING && playerIsPrepared) {
            pausePlayer()
        }
    }

    private fun startCountdown() {
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    currentPositionMillis = mediaPlayer.currentPosition
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    currentTrackTime.text = formattedTime
                    handler.postDelayed(this, 1000)

                } else {
                    mediaPlayer.pause()
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
        if (releaseData.length >= 4) {
            val year = releaseData.substring(0, 4)
            releaseDateTextView.text = year
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

    override fun onBackPressed() {
        super.onBackPressed()
        playerIsPrepared = false
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
        finish()
    }

    private fun preparePlayer() {
        if (!songUrl.isNullOrEmpty() && !playerIsPrepared) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(songUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                playerIsPrepared = true
                progressBar.visibility = View.GONE
                playButton.visibility = View.VISIBLE
                currentTrackTime.visibility = View.VISIBLE
            }
            mediaPlayer.setOnCompletionListener {
                currentPositionMillis = 0
                currentTrackTime.text = String.format("%02d:%02d", 0, 0)
                playerState = STATE_PREPARED
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        startCountdown()
        playButton.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playButton.setImageResource(R.drawable.button_play)
            playerState = STATE_PAUSED
        }
    }

    private fun playbackControl() {
        Log.e("PlayerState", "${playerState}")
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                playButton.isEnabled = true
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                playButton.isEnabled = true
            }
        }
    }

    private fun onPlayPauseButtonClick() {
        if (playerIsPrepared) {
            playbackControl()
        } else {
            playButton.visibility = View.INVISIBLE
            currentTrackTime.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

            preparePlayer()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
                playerIsPrepared = true
                progressBar.visibility = View.GONE
                playButton.visibility = View.VISIBLE
                currentTrackTime.visibility = View.VISIBLE
                playbackControl()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }

    companion object {

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

}