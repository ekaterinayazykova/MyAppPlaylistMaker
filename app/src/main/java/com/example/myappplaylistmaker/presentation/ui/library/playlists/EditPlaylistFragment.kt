package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentCreationBinding
import com.example.myappplaylistmaker.presentation.view_models.edit_playlist.EditPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment: CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()
    private var playlistId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            extractPlaylistById()
            observeState()
    }

    private fun extractPlaylistById() {
        arguments?.getInt("PLAYLIST_ID")?.let { id ->
            this.playlistId = id
            viewModel.setPlaylistId(id)
        }
    }

    private fun observeState() {
        viewModel.playlistData.observe(viewLifecycleOwner) { playlistData ->
            lifecycleScope.launch {
                playlistData.collect { playlist ->
                    binding.editName.setText(playlist.playlistName)
                    binding.editDescription.setText(playlist.playlistDescription)
                    Glide.with(requireContext())
                        .load(if (playlist.imagePath.isNotEmpty()) playlist.imagePath else R.drawable.album_placeholder)
                        .apply(
                            if (playlist.imagePath.isNotEmpty()) {
                                RequestOptions().centerCrop()
                            } else {
                                RequestOptions().centerInside()
                            }
                        )
                        .placeholder(R.drawable.album_placeholder)
                        .error(R.drawable.album_placeholder)
                        .into(binding.addPhoto)
                }
            }
        }
    }
}