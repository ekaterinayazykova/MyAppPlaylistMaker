package com.example.myappplaylistmaker.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.example.myappplaylistmaker.R
import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository

class SettingsOptionsRepositoryImpl (private val context: Context): SettingsOptionsRepository {
    override fun shareApp() {
        val shareText = context.getString(R.string.app_link)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share))
        startActivity(context, chooser, null)
    }

    override fun writeSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subjectWriteSupport))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.textWriteSupport))
        }
        startActivity(context, supportIntent, null)
    }

    override fun acceptTermsOfUse() {
        val acceptionIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.acception_link)))
        startActivity(context, acceptionIntent, null)
    }
}