package com.example.myappplaylistmaker.presentation.ui.library.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.PlaylistItemRecyclerBinding
import com.example.myappplaylistmaker.domain.entity.Playlist

class PlaylistViewHolder(private val binding: PlaylistItemRecyclerBinding,
    private val onPlaylistClick: (Playlist) -> Unit ):
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        val count = model.amountOfTracks ?: 0
        val word = getTrackWord(count)
        binding.playlistName.text = model.playlistName
        binding.tracksAmount.text = "$count $word"
        Glide.with(itemView)
            .load(if (model.imagePath.isNotEmpty()) model.imagePath else R.drawable.album_placeholder)
            .apply(
                if (model.imagePath.isNotEmpty()) {
                    RequestOptions().centerCrop()
                } else {
                    RequestOptions().centerInside()
                }
            )
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(binding.playlistCover)

        itemView.setOnClickListener {
            onPlaylistClick(model)
        }
    }

    fun getTrackWord(count: Int): String {
        val lastTwoDigits = count % 100
        val lastDigit = count % 10
        if (lastTwoDigits in 11..14) {
            return "треков"
        }
        return when (lastDigit) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
    }
}
