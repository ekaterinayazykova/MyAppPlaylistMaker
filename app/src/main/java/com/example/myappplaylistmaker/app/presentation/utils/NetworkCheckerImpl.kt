package com.example.myappplaylistmaker.app.presentation.utils

import android.annotation.SuppressLint
import android.content.Context

class NetworkCheckerImpl(private val context: Context) : NetworkChecker {
    @SuppressLint("MissingPermission")
    override fun isNetworkAvailable(): Boolean {
        return NetworkClass.isNetworkAvailable(context)
    }
}