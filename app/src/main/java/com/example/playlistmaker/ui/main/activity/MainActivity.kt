package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media.activity.MediaActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // переход на экран поиска
        binding.searchCard.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // экран медиатеки
        binding.mediaLibraryCard.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        // экран настроек
        binding.settingsCard.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

//        // 1-ый способ
//        val cardClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Поиск композиции", Toast.LENGTH_SHORT).show()
//            }
//        }
//        searchCard.setOnClickListener(cardClickListener)
//
//        // 2-ой способ через лямбда-выражение
//        searchCard.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Поиск композиции", Toast.LENGTH_SHORT).show()
//        }
    }
}