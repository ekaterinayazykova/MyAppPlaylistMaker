package com.example.myappplaylistmaker.app.di

import android.content.Context
import android.media.MediaPlayer
import com.example.myappplaylistmaker.data.impl.MediaPlayerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.SearchHistoryManagerImpl
import com.example.myappplaylistmaker.data.impl.SettingsOptionsRepositoryImpl
import com.example.myappplaylistmaker.data.impl.ThemeManagerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.TrackRepositoryImpl
import com.example.myappplaylistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchHistoryManagerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchTrackUseCaseImpl
import com.example.myappplaylistmaker.domain.impl.SettingsOptionsInteractorImpl
import com.example.myappplaylistmaker.domain.impl.ThemeManagerInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.repository.ThemeManagerRepository
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {

    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }

    single<SearchHistoryManagerRepository> {
        SearchHistoryManagerImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<SearchHistoryManagerInteractor> { SearchHistoryManagerInteractorImpl(get()) }

    single<MediaPlayerRepository> { MediaPlayerRepositoryImpl(get()) }

    factory<MediaPlayerInteractor> { MediaPlayerInteractorImpl(get()) }

    single {MediaPlayer() }

    single<SettingsOptionsRepository> { SettingsOptionsRepositoryImpl(get()) }

    factory<SettingsOptionsInteractor> { SettingsOptionsInteractorImpl(get()) }

    single<ThemeManagerRepository> { ThemeManagerRepositoryImpl(get()) }

    factory<ThemeManagerInteractor> {ThemeManagerInteractorImpl(get()) }

    factory<SearchTrackUseCase> { SearchTrackUseCaseImpl(get(), get()) }

}

