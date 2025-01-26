package com.example.myappplaylistmaker.core

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.example.myappplaylistmaker.di.appModule
import com.example.myappplaylistmaker.di.dataModule
import com.example.myappplaylistmaker.di.domainModule
import com.example.myappplaylistmaker.domain.interactor.ThemeManagerInteractor
import com.example.myappplaylistmaker.presentation.ui.main.SplashActivity
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    val themeManagerInteractor: ThemeManagerInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, appModule)
        }

        val isNightMode = themeManagerInteractor.getThemeModeState()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

//        val intent = Intent(this, SplashActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//        startActivity(intent)
    }
}