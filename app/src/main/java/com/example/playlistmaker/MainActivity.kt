package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchCard = findViewById<LinearLayout>(R.id.search_card)
        val mediaCard = findViewById<LinearLayout>(R.id.media_card)
        val settingsCard = findViewById<LinearLayout>(R.id.settings_card)

        // переход на экран поиска
        searchCard.setOnClickListener {
            val displayIntent = Intent(this, SeachActivity::class.java)
            startActivity(displayIntent)
        }

        // экран медиатеки
        mediaCard.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        // экран настроек
        settingsCard.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
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