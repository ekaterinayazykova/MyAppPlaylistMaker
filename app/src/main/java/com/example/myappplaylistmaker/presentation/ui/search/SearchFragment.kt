package com.example.myappplaylistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.databinding.FragmentSearchBinding
import com.example.myappplaylistmaker.domain.entity.Track
//import com.example.myappplaylistmaker.presentation.ui.media_player.MediaPlayerActivity
import com.example.myappplaylistmaker.presentation.utils.debounce
import com.example.myappplaylistmaker.presentation.view_models.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var unifiedTrackAdapter: UnifiedTrackAdapter
    private lateinit var debouncedSearch: (String) -> Unit
    private lateinit var debouncedClick: (Track) -> Unit
    private var searchQuery: String = ""
    private var lastQuery: String? = null
    private var isDebounceEnabled = true
    private var isRestoringState = false
    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleKeyboard((requireActivity().window.decorView.rootView), false)
        recyclerView = binding.trackList

        editText()
        observeState()
        cleanHistory()
        debouncedFunc()
        binding.editText.requestFocus()

        if (searchViewModel.searchState.value !is SearchViewModel.State.SuccessSearch) {
            searchViewModel.getDataFromPref()
        }

        unifiedTrackAdapter = UnifiedTrackAdapter(mutableListOf()) { track ->
            if (isDebounceEnabled) {
                isDebounceEnabled = false
                openTrack(track)
                debouncedClick(track)
            }
            searchViewModel.saveToHistory(track)

            if (searchQuery.isEmpty()) {
                searchViewModel.getDataFromPref()
            }
        }

        binding.trackList.layoutManager = LinearLayoutManager(requireContext())
        binding.trackList.adapter = unifiedTrackAdapter

        binding.clearButton.visibility = View.GONE
        binding.clearButton.setOnClickListener {
            toggleKeyboard((requireActivity().window.decorView.rootView), false)
            binding.editText.text.clear()
            searchViewModel.lastQuery = ""
            searchViewModel.getDataFromPref()
            binding.editText.requestFocus()
        }

        binding.editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                isDebounceEnabled = false
                searchViewModel.getDataFromServer(searchQuery)
                toggleKeyboard(v, false)
                true
            } else {
                false
            }
        }

        binding.requestFocusButton.setOnClickListener {
            binding.editText.requestFocus()
            toggleKeyboard(binding.editText, true)
        }


        binding.editText.postDelayed({ toggleKeyboard((binding.editText), true) }, 100)
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        binding.updateButton.setOnClickListener {
            lastQuery?.let { query ->
                performSearch(query)
            }
        }
    }

    private fun performSearch(query: String) {
        val currentState = searchViewModel.searchState.value

        if (currentState is SearchViewModel.State.SuccessSearch &&
            query == searchViewModel.lastQuery
        ) {
            return
        }
        searchViewModel.getDataFromServer(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
        outState.putBoolean(
            IS_CLEAR_BUTTON_VISIBLE_KEY,
            binding.clearButton.visibility == View.VISIBLE
        )
    }

    override fun onResume() {
        super.onResume()
        if (searchQuery.isBlank() || searchQuery.isEmpty()) {
            searchViewModel.getDataFromPref()
        } else {
            searchViewModel.getDataFromServer(searchQuery)
        }
    }

    private fun editText() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                lastQuery = searchQuery

                binding.clearButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                isDebounceEnabled = true

                if (!isRestoringState) {
                    if (searchQuery.isEmpty() && searchQuery.isBlank()) {
                        searchViewModel.setState(SearchViewModel.State.ClearSearchNoQuery)
                    } else {
                        debouncedSearch(searchQuery)
                        SearchViewModel.State.ClearSearch
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cleanHistory() {
        binding.cleanHistoryButton.setOnClickListener {
            searchViewModel.clearedList()
            SearchViewModel.State.ClearSearchNoQuery
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            isRestoringState = true
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            binding.editText.setText(searchQuery)
            binding.editText.setSelection(searchQuery.length)
            isRestoringState = false
        }
    }

    private fun toggleKeyboard(view: View, show: Boolean) {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun debouncedFunc() {
        debouncedSearch = debounce(
            SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) {
            val currentQuery = binding.editText.text.toString()
            if (currentQuery.isBlank()) return@debounce
            if (isDebounceEnabled) {
                performSearch(searchQuery)
            }
            isDebounceEnabled = true
        }

        debouncedClick = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            true
        ) { isDebounceEnabled = true
        }
    }

    private fun openTrack(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("TRACK_DATA", track)
        }
        findNavController().navigate(
            R.id.action_searchFragment_to_mediaPlayerFragment,
            bundle
        )
    }

    private fun observeState() {
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchViewModel.State.EmptyHistory -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.trackList.visibility = View.GONE
                    binding.yourSearch.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.LoadedHistory -> {
                    val tracks = state.listTracks

                    unifiedTrackAdapter.updateTracks(tracks)
                    binding.progressBar.visibility = View.GONE
                    binding.yourSearch.visibility = View.VISIBLE
                    binding.trackList.visibility = View.VISIBLE
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.VISIBLE
                    binding.noInternetPlaceholder.visibility = View.GONE
                }

                is SearchViewModel.State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.trackList.visibility = View.GONE
                    binding.yourSearch.visibility = View.GONE
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.noInternetPlaceholder.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.Error -> {
                    binding.noInternetPlaceholder.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.trackList.visibility = View.GONE
                    toggleKeyboard((requireActivity().window.decorView.rootView), false)
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.yourSearch.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.EmptyMainSearch -> {
                    binding.progressBar.visibility = View.GONE
                    binding.trackList.visibility = View.GONE
                    binding.noSongPlaceholder.visibility = View.VISIBLE
                    binding.yourSearch.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.SuccessSearch -> {
                    val tracks = state.listTracks

                    unifiedTrackAdapter.updateTracks(tracks)
                    binding.progressBar.visibility = View.GONE
                    binding.yourSearch.visibility = View.GONE
                    binding.trackList.visibility = View.VISIBLE
                    toggleKeyboard((requireActivity().window.decorView.rootView), false)
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.ClearSearch -> {
                    binding.yourSearch.visibility = View.VISIBLE
                    binding.trackList.visibility = View.VISIBLE
                    binding.cleanHistoryButton.visibility = View.VISIBLE
                }

                is SearchViewModel.State.ClearSearchNoQuery -> {
                    binding.yourSearch.visibility = View.GONE
                    binding.trackList.visibility = View.GONE
                    binding.cleanHistoryButton.visibility = View.GONE
                }

                is SearchViewModel.State.ClearEditText -> {

                    binding.clearButton.visibility = View.GONE
                    binding.yourSearch.visibility = View.VISIBLE
                    binding.trackList.visibility = View.VISIBLE
                    binding.cleanHistoryButton.visibility = View.VISIBLE
                    binding.noSongPlaceholder.visibility = View.GONE
                    binding.noInternetPlaceholder.visibility = View.GONE
                }
            }
            }
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val IS_CLEAR_BUTTON_VISIBLE_KEY = "isClearButtonVisible"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}
