package com.example.myappplaylistmaker.presentation.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myappplaylistmaker.databinding.FragmentFavtrackBinding

class FavTracksFragment : Fragment() {

    companion object {
        private const val TRACK_ID = "track_id"

        fun newInstance(trackId: String) = FavTracksFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_ID, trackId)
            }
        }
    }

    private var _binding: FragmentFavtrackBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}