package com.example.playlistmaker.domain.sharing.api

import com.example.playlistmaker.domain.sharing.entity.EmailData

interface ContactProvider {

    fun getShareLink(): String
    fun getTermsLink(): String
    fun getContactData(): EmailData
}