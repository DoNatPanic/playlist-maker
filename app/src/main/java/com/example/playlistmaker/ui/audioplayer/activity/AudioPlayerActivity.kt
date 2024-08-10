package com.example.playlistmaker.ui.audioplayer.activity

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.player.entity.PlayerState
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : ComponentActivity() {

    private var trackId: Long = -1

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(trackId)
    }
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // назад
        binding.toolbar.setNavigationOnClickListener {
            onPlayerPause()
            finish()
        }

        // получаем информацию о треке
        val arguments = intent.extras
        if (arguments != null) {
            trackId = arguments.getLong("trackId")
        }

        viewModel.playerStateLiveData().observe(this) { state ->
            renderState(state)
        }

        viewModel.trackLiveData().observe(this) { track ->
            track?.let { render(track) }
        }

        viewModel.elapsedTimeLiveData().observe(this) { elapsedTime ->
            val seconds = elapsedTime / 1000L  // ms -> s
            binding.time.text = String.format("%d:%02d", seconds / 60, seconds % 60)
        }

        binding.playButton.setOnClickListener { _ ->
            onPlayerPlay()
        }

        binding.pauseButton.setOnClickListener { _ ->
            onPlayerPause()
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> {
                binding.playButton.visibility = View.GONE
                binding.pauseButton.visibility = View.VISIBLE
            }

            PlayerState.PAUSED -> {
                binding.playButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
            }

            else -> {
                binding.playButton.visibility = View.VISIBLE
                binding.pauseButton.visibility = View.GONE
            }
        }
    }

    private fun render(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        val trackTimeMillis = track.trackTime
        binding.trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

        val collectionName = track.collectionName
        if (collectionName == null) {
            binding.album.visibility = View.GONE
        } else {
            binding.album.visibility = View.VISIBLE
            binding.album.text = collectionName
        }

        val releaseDate = track.releaseDate
        binding.year.text = releaseDate?.substring(0, 4).toString()

        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country

        fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

        Glide.with(this)
            .load(getCoverArtwork())
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(binding.trackImage)
    }

    private fun onPlayerPlay() {
        viewModel.play()
    }

    private fun onPlayerPause() {
        viewModel.pause()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}