package com.example.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.api.ExternalNavigator
import com.example.playlistmaker.domain.sharing.entity.EmailData

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(shareLink: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareLink)
        intent.setType("text/plain")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openLink(termsLink: String) {
        val url = Uri.parse(termsLink)
        val intent = Intent(Intent.ACTION_VIEW, url)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun sendEmail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, emailData.email)
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        intent.putExtra(Intent.EXTRA_TEXT, emailData.message)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}