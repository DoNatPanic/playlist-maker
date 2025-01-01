package com.example.playlistmaker.ui.audioplayer.activity

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerViewModel
import com.example.playlistmaker.ui.media.activity.PlaylistSmallAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerFragment : Fragment() {

    companion object {
        private const val ARGS_FACT = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_FACT to track)
    }

    private var track: Track? = null

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }
    private lateinit var binding: FragmentAudioPlayerBinding

    private lateinit var playlistAdapter: PlaylistSmallAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var owner = getViewLifecycleOwner()

        playlistAdapter = PlaylistSmallAdapter { playlist ->
            val value = viewModel.onPlaylistClicked(playlist, track!!)
            if (value) {
                val bottomSheetContainer = binding.standardBottomSheet
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    context,
                    "Трек ${track!!.trackName} успешно добавлен в плейлист ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Трек ${track!!.trackName} уже существует в плейлисте ${playlist.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = playlistAdapter

        // назад
        binding.toolbar.setNavigationOnClickListener {
            onPlayerPause()
            findNavController().popBackStack()
        }

        // получаем информацию о треке
        val arguments = arguments

        if (arguments != null) {
            track = arguments.getParcelable<Parcelable>(ARGS_FACT) as Track?
        }
        track?.let { render(track as Track) }

        viewModel.elapsedTimeLiveData().observe(owner) { elapsedTime ->
            val seconds = elapsedTime / 1000L  // ms -> s
            binding.time.text = String.format("%d:%02d", seconds / 60, seconds % 60)
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.isFavouriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.onAddToPlaylistClicked()
            val bottomSheetContainer = binding.standardBottomSheet
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        viewModel.observePlayerState().observe(owner) {
            binding.playButton.isChecked = it.isPlayButtonChecked
        }

        viewModel.isFavouriteLiveData().observe(owner) { isFavourite ->
            binding.isFavouriteButton.isChecked = isFavourite
        }

        viewModel.isAddedLiveData().observe(owner) { isAdded ->
            binding.addToPlaylistButton.isChecked = isAdded
        }

        viewModel.getPlaylistsLiveData()
            .observe(owner) { searchResult -> renderSearchResult(searchResult) }

        // переход на экран создания нового плейлиста
        binding.newPlaylistBtn.setOnClickListener {
            openCreatePlaylistFragment()
        }

        // bottom sheet
        val bottomSheetContainer = binding.standardBottomSheet
        val overlay = binding.overlay
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    // перейти на экран создания плейлиста
    private fun openCreatePlaylistFragment() {
        findNavController().navigate(
            R.id.action_audioPlayerFragment_to_createPlaylistFragment
        )
    }

    private fun renderSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.PlaylistContent -> {
                playlistAdapter.setItems(result.results)
                playlistAdapter.notifyDataSetChanged()
            }

            SearchResult.NotFound -> {
                playlistAdapter.setItems(listOf())
                playlistAdapter.notifyDataSetChanged()
            }

            else -> {}
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
