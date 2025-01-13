package com.example.myappplaylistmaker.app.di

import android.content.Context
import com.example.myappplaylistmaker.app.presentation.utils.NetworkChecker
import com.example.myappplaylistmaker.app.presentation.utils.NetworkCheckerImpl
import com.example.myappplaylistmaker.data.impl.SearchHistoryManagerImpl
import com.example.myappplaylistmaker.data.impl.TrackRepositoryImpl
import com.example.myappplaylistmaker.data.network.ItunesApi
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.data.utils.StringProviderImpl
import com.example.myappplaylistmaker.domain.repository.SearchHistoryManagerRepository
import com.example.myappplaylistmaker.domain.repository.TrackRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin

val dataModule = module {

    single<StringProvider> {
        StringProviderImpl(context = get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            networkChecker = get(),
            itunesService = get()
        )
    }
    single<NetworkChecker> {
        NetworkCheckerImpl(context = get())
    }
    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }
    factory {Gson() }
}