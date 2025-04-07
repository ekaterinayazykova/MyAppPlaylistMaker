package com.example.myappplaylistmaker.presentation.ui.library.playlists

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.presentation.view_models.edit_playlist.EditPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment: CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extractPlaylistById()
        observeState()
        buttonBack()
        setupBinding()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun buttonBack(){
        binding.arrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun savePlaylist() {
        val id = requireArguments().getInt("PLAYLIST_ID")
        val name = binding.editName.text.toString().trim()
        val descriptor = binding.editDescription.text.toString().trim()
        val playlistCover = uploadedCover?.let { saveImageToPrivateStorage(it) } ?: ""
        viewModel.updatePlaylistInfo(id, name, descriptor, playlistCover)
    }

    private fun extractPlaylistById() {
        arguments?.getInt("PLAYLIST_ID")?.let { id ->
            viewModel.loadPlaylistById(id)
        }
    }

    private fun setupBinding() {
        binding.textBack.setText(R.string.button_back)
        binding.buttonCreate.setText(R.string.save_playlist)

        binding.buttonCreate.setOnClickListener {
            savePlaylist()
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.playlistData.collect { playlist ->
                playlist?.let {
                    binding.editName.setText(it.playlistName)
                    binding.editDescription.setText(it.playlistDescription)
                    Glide.with(requireContext())
                        .load(if (!it.imagePath.isNullOrEmpty()) it.imagePath else R.drawable.album_placeholder)
                        .apply(
                            if (!it.imagePath.isNullOrEmpty()) {
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