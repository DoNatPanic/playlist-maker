package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.api.ContactProvider
import com.example.playlistmaker.domain.sharing.api.ExternalNavigator
import com.example.playlistmaker.domain.sharing.api.SharingInteractor
import com.example.playlistmaker.domain.sharing.entity.EmailData

class SharingInteractorImpl(
    private val contactProvider: ContactProvider,
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.sendEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return contactProvider.getShareLink()
    }

    private fun getSupportEmailData(): EmailData {
        return contactProvider.getContactData()
    }

    private fun getTermsLink(): String {
        return contactProvider.getTermsLink()
    }
}