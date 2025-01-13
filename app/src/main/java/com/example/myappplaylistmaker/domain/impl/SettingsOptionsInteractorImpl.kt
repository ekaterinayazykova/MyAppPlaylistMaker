package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor

class SettingsOptionsInteractorImpl(private val repository: SettingsOptionsRepository) :
    SettingsOptionsInteractor {

    override fun shareApp() {
        repository.shareApp()
    }

    override fun writeSupport() {
        repository.writeSupport()
    }

    override fun acceptTermsOfUse() {
        repository.acceptTermsOfUse()
    }
}