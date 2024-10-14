package com.example.myappplaylistmaker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.max

class SearchHistoryManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson: Gson = Gson()
    private val maxHistorySize = 10
    private val historyKey = "search_history"

    fun saveToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()

        history.removeIf { it.artistName == track.artistName && it.trackName == track.trackName }

        history.add(0, track)

        if (history.size > maxHistorySize ) {
            history.removeAt(maxHistorySize)
        }

        Log.d("Search Activity", "history size " + history.size)

        val editor = sharedPreferences.edit()
        editor.putString(historyKey, gson.toJson(history))
        editor.apply()
    }

    fun getSearchHistory(): List<Track> {
        val jsonString = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }
}