package com.example.myappplaylistmaker.presentation.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val option = MutableLiveData<MenuOption?>()
    val chooseOption: LiveData<MenuOption?> get() = option

    fun openSearch() {
        option.value = MenuOption.SEARCH
    }

    fun openLibrary() {
        option.value = MenuOption.LIBRARY
    }

    fun openSettings() {
        option.value = MenuOption.SETTINGS
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