package com.example.myappplaylistmaker.domain.impl

import android.content.Intent
import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor

class SettingsOptionsInteractorImpl(private val repository: SettingsOptionsRepository) :
    SettingsOptionsInteractor {

    override fun createShareIntent(shareMessage: String): Intent {
        return repository.createShareIntent(shareMessage)
    }

    override fun writeSupport() {
        repository.writeSupport()
    }

    override fun acceptTermsOfUse() {
        repository.acceptTermsOfUse()
    }
}