package com.example.myappplaylistmaker.core

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.myappplaylistmaker.data.impl.MediaPlayerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.SearchHistoryManagerImpl
import com.example.myappplaylistmaker.data.impl.SettingsOptionsRepositoryImpl
import com.example.myappplaylistmaker.data.impl.ThemeManagerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.TrackRepositoryImpl
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.data.utils.StringProviderImpl
import com.example.myappplaylistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchHistoryManagerInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.impl.ThemeManagerInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.impl.SearchTrackUseCaseImpl
import com.example.myappplaylistmaker.domain.impl.SettingsOptionsInteractorImpl
import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor
import com.example.myappplaylistmaker.presentation.utils.NetworkChecker
import com.example.myappplaylistmaker.presentation.utils.NetworkCheckerImpl

object Creator {

    lateinit var context: Application

    fun initApplication(application: Application) {
        this.context = application
    }

    private fun provideNetworkChecker(): NetworkChecker {
        return NetworkCheckerImpl(context)
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(provideNetworkChecker())
    }

    fun provideStringProvider(): StringProvider {
        return StringProviderImpl(context)
    }


    fun createPlayer(): MediaPlayerInteractor {
        val mediaPlayerRepository = MediaPlayerRepositoryImpl()
        return MediaPlayerInteractorImpl(mediaPlayerRepository)
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl( provideStringProvider(),provideNetworkClient())
    }

    fun provideTrackUseCase(): SearchTrackUseCase {
        val repository = getTrackRepository()
        return SearchTrackUseCaseImpl(provideStringProvider(), repository)
    }

    fun createThemeManagerInteractor(context: Context) : ThemeManagerInteractor {
        return ThemeManagerInteractorImpl(ThemeManagerRepositoryImpl(context))
    }

    private fun createSearchHistoryManagerRepository(sharedPreferences: SharedPreferences): SearchHistoryManagerRepository {
        return SearchHistoryManagerImpl(sharedPreferences)
    }

    fun createSearchHistoryManagerInteractor(sharedPreferences: SharedPreferences) : SearchHistoryManagerInteractor {
        val repository = createSearchHistoryManagerRepository(sharedPreferences)
        return SearchHistoryManagerInteractorImpl(repository)
    }

    fun createSettingsOptionsInteractor(context: Context) : SettingsOptionsInteractor {
        val repository = createSettingsRepository(context)
        return SettingsOptionsInteractorImpl(repository)
    }

    fun createSettingsRepository(context: Context) : SettingsOptionsRepository {
        return SettingsOptionsRepositoryImpl(context)
    }
}