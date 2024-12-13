package com.example.myappplaylistmaker.domain.entity

import com.example.myappplaylistmaker.data.model.Response

sealed interface Resource<T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error<T>(val message: String) : Resource<T>
}