package com.example.myappplaylistmaker.domain.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}