package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.settings.entity.ThemeSettings
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.darkThemeLiveData().observe(this) { themeSettings ->
            observeDarkTheme(themeSettings)
        }

        binding.themeSwitcher.setOnClickListener { _ -> onDarkThemeSwitched() }

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
        binding.themeSwitcher.isChecked = themeSettings == ThemeSettings.DARK
    }

    private fun onDarkThemeSwitched() {
        viewModel.onDarkThemeSwitched(binding.themeSwitcher.isChecked)
    }
}