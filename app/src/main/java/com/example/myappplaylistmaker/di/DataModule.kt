package com.example.myappplaylistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.myappplaylistmaker.data.converter.TrackDbConverter
import com.example.myappplaylistmaker.data.db.AppDatabase
import com.example.myappplaylistmaker.data.network.ItunesApi
import com.example.myappplaylistmaker.data.network.NetworkClient
import com.example.myappplaylistmaker.data.network.RetrofitNetworkClient
import com.example.myappplaylistmaker.data.utils.StringProvider
import com.example.myappplaylistmaker.data.utils.StringProviderImpl
import com.example.myappplaylistmaker.presentation.utils.NetworkChecker
import com.example.myappplaylistmaker.presentation.utils.NetworkCheckerImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().trackDao() }

    factory { TrackDbConverter() }

    factory {Gson() }

}