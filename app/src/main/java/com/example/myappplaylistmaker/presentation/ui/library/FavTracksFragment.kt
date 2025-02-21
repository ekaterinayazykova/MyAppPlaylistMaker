package com.example.myappplaylistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myappplaylistmaker.databinding.FragmentFavtrackBinding
import com.example.myappplaylistmaker.presentation.view_models.media_library.FavTracksViewModel
import com.example.myappplaylistmaker.presentation.view_models.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavTracksFragment : Fragment() {

    private var _binding: FragmentFavtrackBinding? = null
    private val binding get() = _binding!!
    private val favTracksViewModel by viewModel<FavTracksViewModel>()
    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavtrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        searchViewModel.clearPrefs()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}