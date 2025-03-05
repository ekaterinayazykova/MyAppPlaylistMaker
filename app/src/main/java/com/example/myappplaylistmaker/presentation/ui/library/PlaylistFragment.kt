package com.example.myappplaylistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentPlaylistBinding
import com.example.myappplaylistmaker.presentation.view_models.media_library.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylist.setOnClickListener{
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment2)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}