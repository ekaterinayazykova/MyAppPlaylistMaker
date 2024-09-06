package com.example.myappplaylistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var requestFocusButton: Button
    private lateinit var buttonClear: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private var searchQuery: String = ""

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
        }

        editText = findViewById(R.id.edit_text)
        requestFocusButton = findViewById(R.id.requestFocusButton)
        recyclerView = findViewById(R.id.track_list)

        val tracks = listOf(
            Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )

        val adapter = SearchAdapter(tracks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.requestLayout()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchQuery = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        requestFocusButton.setOnClickListener {
            editText.requestFocus()
            showKeyboard(editText)
        }

        editText.requestFocus()
        editText.postDelayed({ showKeyboard(editText) }, 100)

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
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
}