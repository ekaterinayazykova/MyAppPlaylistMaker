package com.example.myappplaylistmaker

import android.os.Bundle
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



        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra<Track>("track")
        track?.let { fetchTrackData(it) }
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

    }