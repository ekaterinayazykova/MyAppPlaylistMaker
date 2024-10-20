package com.example.myappplaylistmaker

import android.content.Intent
import android.os.Bundle
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var searchHistoryManager: SearchHistoryManager
    private lateinit var yourSearch: TextView
    private lateinit var cleanHistoryButton: Button


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

        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        unifiedTrackAdapter = UnifiedTrackAdapter(mutableListOf()) { track ->
            openTrack(track)
            searchHistoryManager.saveToHistory(track)
            if (searchQuery.isEmpty())
                unifiedTrackAdapter.updateTracks(searchHistoryManager.getSearchHistory())
        }

        searchHistoryManager = SearchHistoryManager(this)

        val historyTracks = searchHistoryManager.getSearchHistory()

        if (historyTracks.isNotEmpty()) {
            unifiedTrackAdapter.updateTracks(historyTracks)
            yourSearch.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            noSongPlaceholder.visibility = View.GONE
            cleanHistoryButton.visibility = View.VISIBLE
        } else {
            noSongPlaceholder.visibility = View.GONE
            recyclerView.visibility = View.GONE
            yourSearch.visibility = View.GONE
            cleanHistoryButton.visibility = View.GONE
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
            noInternetPlaceholder.visibility =View.GONE
            editText.requestFocus()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        noSongPlaceholder.visibility = View.GONE
                        yourSearch.visibility = View.GONE
                        cleanHistoryButton.visibility = View.GONE
                    } else {
                        noSongPlaceholder.visibility = View.VISIBLE
                    }
                }
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
                performSearch(query) { trackFound ->
                    if (trackFound) {
                        noSongPlaceholder.visibility = View.GONE
                        yourSearch.visibility = View.GONE
                        cleanHistoryButton.visibility = View.GONE
                    } else {
                        noInternetPlaceholder.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun performSearch(query: String, callback: (Boolean) -> Unit) {

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.iTunesLink))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ItunesApi::class.java)

        val call = api.search(query)

        call.enqueue(object : Callback<TrackResponse> {

            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                Log.d("SearchActivity", "Response code: ${response.code()}")
                noInternetPlaceholder.visibility = View.GONE

                if (response.isSuccessful) {

                    val trackResponse = response.body()

                    val tracks = trackResponse?.results ?: emptyList()


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
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("SearchActivity", "Network call failed: ${t.message}")
                noInternetPlaceholder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                hideKeyboard(window.decorView.rootView)
                noSongPlaceholder.visibility = View.GONE
                yourSearch.visibility = View.GONE
                cleanHistoryButton.visibility = View.GONE
                lastQuery = searchQuery
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
    }

    private fun openTrack (track: Track) {
        val trackIntent = Intent(this, TrackActivity:: class.java)
        trackIntent.putExtra("track",track)
        startActivity(trackIntent)
    }

}