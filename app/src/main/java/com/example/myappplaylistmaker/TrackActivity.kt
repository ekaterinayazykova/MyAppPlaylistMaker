package com.example.myappplaylistmaker

import android.app.usage.NetworkStats.Bucket.STATE_DEFAULT
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
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

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var screenReceiver: ScreenReceiver

    private var songBridge: String = ""

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
        addToFavoritesButton = findViewById(R.id.button_like)
        currentTrackTime = findViewById(R.id.track_duration)


        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra<Track>("track")
        track?.let { fetchTrackData(it) }

        screenReceiver = ScreenReceiver()
        screenReceiver.playbackCallback = {
            pausePlayer()
        }

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)

        val track = intent.getSerializableExtra(TRACK_DATA) as? Track

        track?.let {
            fetchTrackData(it)
            preparePlayer()
        }



    }

    private fun fetchTrackData(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = formatTrackTime(track.trackTimeMillis)
        countryTextView.text = track.country

        if (!track.collectionName.isNullOrEmpty()) {
            collectionNameTextView.text = track.collectionName
            collectionNameTextView.visibility = View.VISIBLE
        } else {
            collectionNameTextView.visibility = View.GONE
        }

        val releaseData = track.releaseDate
        if (releaseData.length >= 4) {
            val year = releaseData.substring(0,4)
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

    companion object {
        const val FORMAT_TIME_TS = "%02d:%02d"
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    }