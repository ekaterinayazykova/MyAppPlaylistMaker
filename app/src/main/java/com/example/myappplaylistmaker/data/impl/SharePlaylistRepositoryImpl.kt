package com.example.myappplaylistmaker.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.repository.SharePlaylistRepository

class SharePlaylistRepositoryImpl(private val context: Context): SharePlaylistRepository {
    override fun sharePlaylist(shareMessage: String) {
        val shareText = shareMessage
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,shareText)
            type ="text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.sharePlaylist)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(context, chooser, null)
    }
}