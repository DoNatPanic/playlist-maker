package com.example.playlistmaker.domain.sharing.api

import com.example.playlistmaker.domain.sharing.entity.EmailData

interface ExternalNavigator {

    fun shareLink(shareLink: String)
    fun openLink(termsLink: String)
    fun sendEmail(emailData: EmailData)
}