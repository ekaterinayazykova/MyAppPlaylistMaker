package com.example.myappplaylistmaker.core

import android.content.Context
import android.content.SharedPreferences
import com.example.myappplaylistmaker.data.impl.MediaPlayerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.SearchHistoryManagerImpl
import com.example.myappplaylistmaker.data.impl.ThemeManagerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.TrackRepositoryImpl
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myappplaylistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchHistoryManagerInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.impl.ThemeManagerInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.impl.SearchTrackUseCaseImpl
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase

object Creator {

    fun createPlayer(): MediaPlayerInteractor {
        val mediaPlayerRepository = MediaPlayerRepositoryImpl()
        return MediaPlayerInteractorImpl(mediaPlayerRepository)
    }

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(context, provideNetworkClient())
    }

    fun provideTrackUseCase(context: Context): SearchTrackUseCase {
        val repository = getTrackRepository(context)
        return SearchTrackUseCaseImpl(context, repository)
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
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

}