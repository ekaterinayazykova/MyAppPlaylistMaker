package com.example.myappplaylistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myappplaylistmaker.presentation.ui.root.RootActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val intent = Intent(this, RootActivity::class.java)
        startActivity(intent)
        finish()
    }
}

