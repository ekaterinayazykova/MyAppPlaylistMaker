package com.example.myappplaylistmaker.domain.impl

import com.example.myappplaylistmaker.domain.repository.SharePlaylistRepository
import com.example.myappplaylistmaker.domain.use_case.SharePlaylistUseCase

class SharePlaylistUseCaseImpl(
    private val sharePlaylistRepository: SharePlaylistRepository): SharePlaylistUseCase {
    override fun sharePlaylist(shareMessage: String) {
        sharePlaylistRepository.sharePlaylist(shareMessage)
    }
}