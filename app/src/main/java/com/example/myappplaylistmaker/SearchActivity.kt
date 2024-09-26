package com.example.myappplaylistmaker

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
import android.widget.Toast
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
    private lateinit var requestFocusButton: Button
    private lateinit var buttonClear: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var noSongPlaceholder: LinearLayout
    private lateinit var noInternetPlaceholder: LinearLayout
    private var lastQuery: String? = null
    private lateinit var updateButton: Button
    private var searchQuery: String = ""
    private lateinit var adapter: SearchAdapter

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val IS_CLEAR_BUTTON_VISIBLE_KEY = "isClearButtonVisible"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        findViewById<ImageView>(R.id.arrow).setOnClickListener {
            finish()
        }

        buttonClear = findViewById(R.id.clearButton)
        buttonClear.visibility = View.GONE
        buttonClear.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.text.clear()
            buttonClear.visibility = View.GONE

            editText.requestFocus()
        }

        editText = findViewById(R.id.edit_text)
        requestFocusButton = findViewById(R.id.requestFocusButton)
        recyclerView = findViewById(R.id.track_list)
        noSongPlaceholder = findViewById(R.id.no_song_placeholder)
        noInternetPlaceholder = findViewById(R.id.no_internet_placeholder)

        adapter = SearchAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.requestLayout()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonClear.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (s.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    noSongPlaceholder.visibility = View.GONE
                } else {
                    noSongPlaceholder.visibility = View.GONE
                }
                searchQuery = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        noSongPlaceholder.visibility = View.GONE
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

        updateButton = findViewById(R.id.update_button)
        updateButton.setOnClickListener {
            lastQuery?.let { query ->
                performSearch(query) { trackFound ->
                    if (trackFound) {
                        noSongPlaceholder.visibility = View.GONE
                    } else {
                        noSongPlaceholder.visibility = View.VISIBLE
                    }
                }
            }
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
        outState.putBoolean(IS_CLEAR_BUTTON_VISIBLE_KEY, buttonClear.visibility == View.VISIBLE)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
        val isVisible = savedInstanceState.getBoolean(IS_CLEAR_BUTTON_VISIBLE_KEY, false)
        buttonClear.visibility = if (isVisible) View.VISIBLE else View.GONE
        editText.requestFocus()
        showKeyboard(editText)
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

    private val itunesBaseUrl = "https://itunes.apple.com/"

    private fun performSearch(query: String, callback: (Boolean) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ItunesApi::class.java)

        val call = api.search(query)
        call.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                Log.d("SearchActivity", "Response received for query: $query")
                Log.d("SearchActivity", "Response Code: ${response.code()}")
                noInternetPlaceholder.visibility = View.GONE

                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    Log.d("SearchActivity", "Response Body: ${trackResponse.toString()}")

                    val tracks = trackResponse?.results ?: emptyList()
                    Log.d("SearchActivity", "Tracks found: ${tracks.size}")

                    if (tracks.isNotEmpty()) {
                        adapter.updateTracks(tracks)
                        recyclerView.visibility = View.VISIBLE
                        noSongPlaceholder.visibility = View.GONE
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                        callback(true)
                    } else {
                        recyclerView.visibility = View.GONE
                        noSongPlaceholder.visibility = View.VISIBLE
                        callback(false)
                    }

                } else {
                    Log.e("SearchActivity", "Response Error: ${response.code()} - ${response.errorBody()?.string()}")
                    recyclerView.visibility = View.GONE
                    noSongPlaceholder.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("SearchActivity", "Network Error: ${t.message}")
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
                noInternetPlaceholder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                noSongPlaceholder.visibility = View.GONE
                lastQuery = searchQuery
            }
        })
    }
}
