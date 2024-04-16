package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backBtn = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        val shareBtn = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.share_btn)
        val supportBtn = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.support_btn)
        val agreementBtn = findViewById<com.google.android.material.textview.MaterialTextView>(R.id.agreement_btn)

        // переход на главный экран
        backBtn.setNavigationOnClickListener {
            finish()
        }

        // поделиться
        shareBtn.setOnClickListener {
            val urlString = getText(R.string.share_text)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, urlString);
            intent.setType("text/plain");
            startActivity(intent)
        }

        // отправить письмо
        supportBtn.setOnClickListener {
            val subject = getText(R.string.mail_subject)
            val message = getText(R.string.mail_text)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getText(R.string.email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        // перейти на сайт
        agreementBtn.setOnClickListener {
            val url = Uri.parse(getText(R.string.agreement_url).toString())
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}