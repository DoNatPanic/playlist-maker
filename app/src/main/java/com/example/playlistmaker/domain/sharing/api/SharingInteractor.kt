package com.example.playlistmaker.domain.sharing.api

interface SharingInteractor {
    fun shareApp()
    fun sharePlaylist(message: String)
    fun openTerms()
    fun openSupport()
}