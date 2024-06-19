package com.example.playlistmaker

import android.media.MediaPlayer
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var backBtn: MaterialToolbar
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackImage: ImageView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private var mediaPlayer = MediaPlayer()
    private var previewUrl: String? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY = 1000L

        private const val PREVIEW_TIME = 30_000L
    }

    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private var elapsedTime: Long = 0L

    private fun startTimer() {
        // Запоминаем время начала таймера
        val startTime = System.currentTimeMillis()

        // И отправляем задачу в Handler
        // Число секунд переводим в миллисекунды
        mainThreadHandler?.post(
            createUpdateTimerTask(startTime, PREVIEW_TIME, elapsedTime)
        )
    }

    private fun createUpdateTimerTask(startTime: Long, duration: Long, elTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                // Сколько прошло времени с момента запуска таймера
                elapsedTime = System.currentTimeMillis() - startTime + elTime

                if (elapsedTime <= duration) {
                    if (playerState == STATE_PLAYING) {
                        // Если всё ещё отсчитываем секунды —
                        // обновляем UI и снова планируем задачу
                        val seconds = elapsedTime / DELAY
                        trackDuration?.text = String.format("%d:%02d", seconds / 60, seconds % 60)
                        mainThreadHandler?.postDelayed(this, DELAY)
                    }
                } else {
                    // Если таймер окончен, останавливаем воспроизведение
                    pausePlayer()
                    resetTimerUI()
                    elapsedTime = 0L
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // Создаём Handler, привязанный к ГЛАВНОМУ потоку
        mainThreadHandler = Handler(Looper.getMainLooper())

        backBtn = findViewById(R.id.toolbar)

        // назад
        backBtn.setNavigationOnClickListener {
            finish()
        }

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        trackDuration = findViewById(R.id.time)
        trackImage = findViewById(R.id.trackImage)
        album = findViewById(R.id.album)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)

        // получаем информацию о треке
        val arguments = intent.extras
        if (arguments != null) {
            trackName.text = arguments.getString("trackName")
            artistName.text = arguments.getString("artistName")

            val trackTimeMillis = arguments.getInt("trackTimeMillis")
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)


            val collectionName = arguments.getString("collectionName")
            if (collectionName == null) {
                album.visibility = View.GONE
            } else {
                album.visibility = View.VISIBLE
                album.text = collectionName
            }

            val releaseDate = arguments.getString("releaseDate")
            year.text = releaseDate?.substring(0, 4).toString()

            genre.text = arguments.getString("primaryGenreName")
            country.text = arguments.getString("country")
            previewUrl = arguments.getString("previewUrl")

            val artworkUrl = arguments.getString("artworkUrl100")
            fun getCoverArtwork() = artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")

            val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

            Glide.with(this)
                .load(getCoverArtwork())
                .placeholder(R.drawable.track_image)
                .error(R.drawable.track_image)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(trackImage)
        }

        preparePlayer()

        playButton.setOnClickListener {
            if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
                startPlayer()
                startTimer() // запускаем таймер
            }
        }

        pauseButton.setOnClickListener {
            if (playerState == STATE_PLAYING) {
                pausePlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            resetTimerUI()
        }
        mediaPlayer.setOnCompletionListener {
            playButton.visibility = View.VISIBLE
            playerState = STATE_PREPARED
        }
    }

    private fun resetTimerUI() {
        trackDuration?.text = "0:00"
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        playerState = STATE_PAUSED
    }
}