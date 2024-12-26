package com.example.myappplaylistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myappplaylistmaker.presentation.utils.NetworkClass
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.presentation.utils.Utils

class UnifiedTrackAdapter(
    private var tracks: List<Track> = emptyList(),
    private val onTrackClick: (Track) -> Unit
    ) : RecyclerView.Adapter<UnifiedTrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_duration)
        private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)

        fun getTrackDuration(trackTimeMillis: Long,): String {
            val totalSeconds = trackTimeMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        fun bind(model: Track, isTrackAddedToHistory: Boolean = false) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = getTrackDuration(model.trackTimeMillis)
            artistName.requestLayout()

            if (NetworkClass.isNetworkAvailable(itemView.context)) {
                Glide.with(itemView)
                    .load(model.artworkUrl100)
                    .fitCenter()
                    .placeholder(R.drawable.icon_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
                    .into(trackCover)
            } else {
                trackCover.setImageResource(R.drawable.icon_placeholder)
            }

            itemView.setOnClickListener {
                onTrackClick.invoke(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            onTrackClick(tracks[position])
        }
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}