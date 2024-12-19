package com.example.myappplaylistmaker.presentation.UI.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappplaylistmaker.core.Creator
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.consumer.Consumer
import com.example.myappplaylistmaker.domain.consumer.ConsumerData
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.presentation.UI.media_player.MediaPlayerActivity
import com.google.android.material.button.MaterialButton
class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var buttonClear: MaterialButton
    private lateinit var buttonUpdate: MaterialButton
    private lateinit var requestFocusButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var noSongPlaceholder: LinearLayout
    private lateinit var noInternetPlaceholder: LinearLayout
    private lateinit var unifiedTrackAdapter: UnifiedTrackAdapter
    private var searchQuery: String = ""
    private var lastQuery: String? = null
    private lateinit var yourSearch: TextView
    private lateinit var cleanHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryManager: SearchHistoryManagerInteractor

    private val handler = Handler(Looper.getMainLooper())

    private val getTrackListUseCase = Creator.provideTrackUseCase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        hideKeyboard(window.decorView.rootView)

        editText = findViewById(R.id.edit_text)
        requestFocusButton = findViewById(R.id.requestFocusButton)
        buttonClear = findViewById(R.id.clearButton)
        buttonUpdate = findViewById(R.id.update_button)
        recyclerView = findViewById(R.id.track_list)
        noSongPlaceholder = findViewById(R.id.no_song_placeholder)
        noInternetPlaceholder = findViewById(R.id.no_internet_placeholder)
        yourSearch = findViewById(R.id.yourSearch)
        cleanHistoryButton = findViewById(R.id.clean_history_button)
        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        searchHistoryManager = Creator.createSearchHistoryManagerInteractor(sharedPreferences)
        val historyTracks = searchHistoryManager.getSearchHistory()


        unifiedTrackAdapter = UnifiedTrackAdapter(mutableListOf()) { track ->
            openTrack(track)
            searchHistoryManager.saveToHistory(track)
            if (searchQuery.isEmpty())
                unifiedTrackAdapter.updateTracks(searchHistoryManager.getSearchHistory())
        }


        if (searchHistoryManager.getSearchHistory().isNotEmpty()) {
            unifiedTrackAdapter.updateTracks(searchHistoryManager.getSearchHistory())
            yourSearch.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            noSongPlaceholder.visibility = View.GONE
            cleanHistoryButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            noSongPlaceholder.visibility = View.GONE
            recyclerView.visibility = View.GONE
            yourSearch.visibility = View.GONE
            cleanHistoryButton.visibility = View.GONE
            progressBar.visibility = View.GONE

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = unifiedTrackAdapter

        cleanHistoryButton.setOnClickListener {
            searchHistoryManager.clearHistory()
            yourSearch.visibility = View.GONE
            cleanHistoryButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()

                buttonClear.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                if (searchQuery.isEmpty()) {
                    yourSearch.visibility = View.VISIBLE
                    unifiedTrackAdapter.updateTracks(searchHistoryManager.getSearchHistory())
                    recyclerView.visibility = View.VISIBLE
                    cleanHistoryButton.visibility = View.VISIBLE
                } else {
                    searchDebounce()
                    yourSearch.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    cleanHistoryButton.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        buttonClear.visibility = View.GONE
        buttonClear.setOnClickListener {
            hideKeyboard(window.decorView.rootView)
            editText.text.clear()
            buttonClear.visibility = View.GONE
            yourSearch.visibility = View.VISIBLE
            unifiedTrackAdapter.updateTracks(searchHistoryManager.getSearchHistory())
            recyclerView.visibility = View.VISIBLE
            cleanHistoryButton.visibility = View.VISIBLE
            noSongPlaceholder.visibility = View.GONE
            noInternetPlaceholder.visibility = View.GONE
            editText.requestFocus()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery)
                true
            } else {
                false
            }
        }

        requestFocusButton.setOnClickListener {
            editText.requestFocus()
            showKeyboard(editText)
        }
        editText.requestFocus()
        editText.postDelayed({ showKeyboard(editText) }, 100)
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        buttonUpdate.setOnClickListener {
            lastQuery?.let { query ->
                noInternetPlaceholder.visibility = View.GONE
                performSearch(query)
            }
        }
    }

    private val searchRunnable = Runnable {
        performSearch(searchQuery)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE


        getTrackListUseCase.execute(query, object : Consumer<List<Track>> {
            override fun consume(data: ConsumerData<List<Track>>) {
                runOnUiThread {
                    when (data) {
                        is ConsumerData.Data -> {
                            noInternetPlaceholder.visibility = View.GONE
                            progressBar.visibility = View.GONE

                            val tracks = data.value

                            if (tracks.isNotEmpty()) {
                                unifiedTrackAdapter.updateTracks(tracks)
                                recyclerView.visibility = View.VISIBLE
                                hideKeyboard(window.decorView.rootView)
                                noSongPlaceholder.visibility = View.GONE
                                yourSearch.visibility = View.GONE
                                cleanHistoryButton.visibility = View.GONE
                            } else {
                                recyclerView.visibility = View.GONE
                                noSongPlaceholder.visibility = View.VISIBLE
                            }
                        }

                        is ConsumerData.Error -> {
                            noInternetPlaceholder.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            hideKeyboard(window.decorView.rootView)
                            noSongPlaceholder.visibility = View.GONE
                            yourSearch.visibility = View.GONE
                            cleanHistoryButton.visibility = View.GONE
                            lastQuery = query
                        }
                    }
                }
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
        outState.putBoolean(IS_CLEAR_BUTTON_VISIBLE_KEY, buttonClear.visibility == View.VISIBLE)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            editText.setText(searchQuery)
            editText.setSelection(searchQuery.length)
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val IS_CLEAR_BUTTON_VISIBLE_KEY = "isClearButtonVisible"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private fun openTrack(track: Track) {
        val trackIntent = Intent(this, MediaPlayerActivity::class.java)
        trackIntent.putExtra("track", track)
        startActivity(trackIntent)
    }

}