package com.example.playlistmaker.ui.audioplayer.activity

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val ARGS_FACT = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_FACT to track)
    }

    private var track: Track? = null

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
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
            track = arguments.getParcelable<Parcelable>(ARGS_FACT) as Track?
        }
        track?.let { render(track as Track) }

        viewModel.elapsedTimeLiveData().observe(this) { elapsedTime ->
            val seconds = elapsedTime / 1000L  // ms -> s
            binding.time.text = String.format("%d:%02d", seconds / 60, seconds % 60)
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.isFavouriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.observePlayerState().observe(this) {
            binding.playButton.isChecked = it.isPlayButtonChecked
        }

        viewModel.isFavouriteLiveData().observe(this) { isFavourite ->
            binding.isFavouriteButton.isChecked = isFavourite
        }
    }

    private fun render(track: Track) {
        binding.isFavouriteButton.isChecked = track.isFavorite

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

        fun getCoverArtwork() = track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

        val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

        Glide.with(this)
            .load(getCoverArtwork())
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(binding.trackImage)
    }

    private fun onPlayerPause() {
        viewModel.pausePlayer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}