package com.example.myappplaylistmaker.data.utils

import android.content.Context

class StringProviderImpl(private val context: Context) : StringProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

}