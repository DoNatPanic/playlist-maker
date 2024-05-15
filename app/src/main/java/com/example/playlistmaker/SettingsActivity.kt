package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val backBtn = findViewById<MaterialToolbar>(R.id.toolbar)
        val shareBtn = findViewById<MaterialTextView>(R.id.shareBtn)
        val supportBtn = findViewById<MaterialTextView>(R.id.supportBtn)
        val agreementBtn = findViewById<MaterialTextView>(R.id.agreementBtn)

        themeSwitcher.isChecked = DARK_MODE_VALUE

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            DARK_MODE_VALUE = checked
            (applicationContext as App).saveShared(checked)
        }

        // переход на главный экран
        backBtn.setNavigationOnClickListener {
            finish()
        }

        // поделиться
        shareBtn.setOnClickListener {
            val urlString = getText(R.string.share_text)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, urlString)
            intent.setType("text/plain")
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
