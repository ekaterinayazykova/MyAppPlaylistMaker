package com.example.myappplaylistmaker.domain.interactor

import android.content.Intent

interface SettingsOptionsInteractor {
    fun createShareIntent(shareMessage: String): Intent
    fun writeSupport()
    fun acceptTermsOfUse()
}