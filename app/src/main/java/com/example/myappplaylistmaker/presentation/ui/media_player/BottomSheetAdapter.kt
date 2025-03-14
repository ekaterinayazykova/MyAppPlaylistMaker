package com.example.myappplaylistmaker.presentation.ui.media_player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplaylistmaker.databinding.PlaylistBottomItemBinding
import com.example.myappplaylistmaker.domain.entity.Playlist

class BottomSheetAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
): RecyclerView.Adapter<BottomSheetViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistBottomItemBinding.inflate(inflater, parent, false)
        return BottomSheetViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
    fun updatePlaylist(newPlaylist: List<Playlist>) {
        playlists = newPlaylist
        notifyDataSetChanged()
    }
}
