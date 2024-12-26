package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.use_case.SettingsOptionsUseCase

class SettingsOptionsUseCaseImpl(private val repository: SettingsOptionsRepository) : SettingsOptionsUseCase {

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