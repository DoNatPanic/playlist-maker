package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.settings.entity.ThemeSettings
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.darkThemeLiveData().observe(this) { themeSettings ->
            observeDarkTheme(themeSettings)
        }

        binding.themeSwitcher.setOnClickListener { _ -> onDarkThemeSwitched() }

        // переход на главный экран
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // поделиться
        binding.shareBtn.setOnClickListener {
            viewModel.shareApp()
        }

        // отправить письмо
        binding.supportBtn.setOnClickListener {
            viewModel.openSupport()
        }

        // перейти на сайт
        binding.agreementBtn.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun observeDarkTheme(themeSettings: ThemeSettings) {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = themeSettings == ThemeSettings.DARK
    }

    private fun onDarkThemeSwitched() {
        viewModel.onDarkThemeSwitched(binding.themeSwitcher.isChecked)
    }
}
