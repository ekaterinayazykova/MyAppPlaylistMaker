package com.example.myappplaylistmaker.di

import android.media.MediaPlayer
import com.example.myappplaylistmaker.data.db.impl.FavTracksRepositoryImpl
import com.example.myappplaylistmaker.data.db.impl.PlaylistRepositoryImpl
import com.example.myappplaylistmaker.data.db.impl.TracksToPlaylistRepositoryImpl
import com.example.myappplaylistmaker.data.impl.MediaPlayerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.SearchHistoryManagerImpl
import com.example.myappplaylistmaker.data.impl.SettingsOptionsRepositoryImpl
import com.example.myappplaylistmaker.data.impl.ThemeManagerRepositoryImpl
import com.example.myappplaylistmaker.data.impl.TrackRepositoryImpl
import com.example.myappplaylistmaker.domain.db.FavTracksRepository
import com.example.myappplaylistmaker.domain.db.PlaylistRepository
import com.example.myappplaylistmaker.domain.db.TracksToPlaylistRepository
import com.example.myappplaylistmaker.domain.impl.FavTracksInteractorImpl
import com.example.myappplaylistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.PlaylistInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchHistoryManagerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.SearchTrackUseCaseImpl
import com.example.myappplaylistmaker.domain.impl.SettingsOptionsInteractorImpl
import com.example.myappplaylistmaker.domain.impl.ThemeManagerInteractorImpl
import com.example.myappplaylistmaker.domain.impl.TracksToPlaylistInteractorImpl
import com.example.myappplaylistmaker.domain.interactor.FavTracksInteractor
import com.example.myappplaylistmaker.domain.interactor.MediaPlayerInteractor
import com.example.myappplaylistmaker.domain.interactor.PlaylistInteractor
import com.example.myappplaylistmaker.domain.interactor.SearchHistoryManagerInteractor
import com.example.myappplaylistmaker.domain.interactor.SettingsOptionsInteractor
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.domain.interactor.TracksToPlaylistInteractor
import com.example.myappplaylistmaker.domain.repository.MediaPlayerRepository
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.example.myappplaylistmaker.domain.repository.SettingsOptionsRepository
import com.example.myappplaylistmaker.domain.repository.ThemeManagerRepository
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.example.myappplaylistmaker.domain.use_case.SearchTrackUseCase
import org.koin.dsl.module

val domainModule = module {

    single<TrackRepository> { TrackRepositoryImpl(get(), get(), get()) }

    single<SearchHistoryManagerRepository> {
        SearchHistoryManagerImpl(
            sharedPreferences = get(),
            gson = get(),
            appDatabase = get()
        )
    }

    single<SearchHistoryManagerInteractor> {
        SearchHistoryManagerInteractorImpl(get())
    }

    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    single { MediaPlayer() }

    single<SettingsOptionsRepository> {
        SettingsOptionsRepositoryImpl(get())
    }

    factory<SettingsOptionsInteractor> {
        SettingsOptionsInteractorImpl(get())
    }

    single<ThemeManagerRepository> {
        ThemeManagerRepositoryImpl(get())
    }

    factory<ThemeManagerInteractor> {
        ThemeManagerInteractorImpl(get())
    }

    factory<SearchTrackUseCase> {
        SearchTrackUseCaseImpl(get(), get())
    }

    single<FavTracksInteractor> {
        FavTracksInteractorImpl(get())
    }
    single<FavTracksRepository> {
        FavTracksRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

    single<TracksToPlaylistRepository> {
        TracksToPlaylistRepositoryImpl(get(), get())
    }

    single<TracksToPlaylistInteractor> {
        TracksToPlaylistInteractorImpl(get())
    }
    
}
