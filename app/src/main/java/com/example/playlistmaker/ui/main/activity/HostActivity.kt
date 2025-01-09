package com.example.playlistmaker.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityHostBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.createPlaylistFragment, R.id.audioPlayerFragment, R.id.playlistInfoFragment -> {
                    bottomNavigationView.isVisible = false
                }

                else -> {
                    bottomNavigationView.isVisible = true
                }
            }
        }
        bottomNavigationView.setupWithNavController(navController)
    }
}