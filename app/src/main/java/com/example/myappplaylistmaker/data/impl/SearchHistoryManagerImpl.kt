package com.example.myappplaylistmaker.data.impl

import android.content.SharedPreferences
import android.util.Log
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.domain.entity.Track
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryManagerImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryManagerRepository {

    init {
        Log.d("Koin", "SearchHistoryManagerImpl создан, appDatabase = $appDatabase")
    }

    private val maxHistorySize = 10
    private val historyKey = "search_history"

    override fun saveToHistory(track: Track) {
        val history = getSearchHistorySync().toMutableList()
        history.removeIf { it.artistName == track.artistName && it.trackName == track.trackName }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(maxHistorySize)
        }
        Log.d("Search Activity", "history size " + history.size)
        val editor = sharedPreferences.edit()
        editor.putString(historyKey, gson.toJson(history))
        editor.apply()
    }

    override suspend fun getSearchHistory(): List<Track>  = withContext(Dispatchers.IO){
        val jsonString = sharedPreferences.getString(historyKey, null) ?: return@withContext emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        val resultTracks = gson.fromJson<List<Track>>(jsonString, type)
        Log.d("TAG", "$jsonString")

        val favTrakcsId = appDatabase.trackDao().getTrackId()

        resultTracks.map{ track ->
            Log.d("TAG", "track = $track")
            track.copy(isFavorite = track.trackId in favTrakcsId)
        }
    }

    override fun getSearchHistorySync(): List<Track> {
        val jsonString = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    override fun clearPrefs() {
        sharedPreferences.edit().clear().apply()
    }

}