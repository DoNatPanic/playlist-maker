package com.example.playlistmaker.data.sharing

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.api.ContactProvider
import com.example.playlistmaker.domain.sharing.entity.EmailData

class ContactProviderImpl(
    private val context: Context
) : ContactProvider {

    override fun getShareLink(): String {
        return context.getString(R.string.share_text)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_url)
    }

    override fun getContactData(): EmailData {
        return EmailData(
            arrayOf(context.getString(R.string.email)),
            context.getString(R.string.mail_subject),
            context.getString(R.string.mail_text)
        )
    }
}