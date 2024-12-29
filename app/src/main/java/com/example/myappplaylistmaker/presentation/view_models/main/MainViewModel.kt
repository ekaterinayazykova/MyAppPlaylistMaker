package com.example.myappplaylistmaker.presentation.view_models.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val mainOption = MutableLiveData<MenuOption?>()
    val chooseOption: LiveData<MenuOption?> get() = mainOption

    fun openSearch() {
        mainOption.value = MenuOption.SEARCH
    }

    fun openLibrary() {
        mainOption.value = MenuOption.LIBRARY
    }

    fun openSettings() {
        mainOption.value = MenuOption.SETTINGS
    }

    override fun onCleared() {
        Log.e("AAA", "VM cleared")
        super.onCleared()
    }

    sealed class MenuOption {
        object SEARCH : MenuOption()
        object LIBRARY: MenuOption()
        object SETTINGS : MenuOption()
    }

}