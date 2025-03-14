package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplaylistmaker.databinding.PlaylistItemRecyclerBinding
import com.example.myappplaylistmaker.domain.entity.Playlist

class PlaylistAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
): RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemRecyclerBinding.inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener{
            onPlaylistClick(playlist)
        }
    }
    fun updatePlaylist(newPlaylist: List<Playlist>) {
        playlists = newPlaylist
        notifyDataSetChanged()
    }
}
