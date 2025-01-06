package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.media.view_model.PlaylistInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {
    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistId"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

    private var playlistId: Long = 0

    private var playlistTracksList: List<Track> = listOf()

    private val viewModel: PlaylistInfoViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var binding: FragmentPlaylistInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var owner = getViewLifecycleOwner()

        // назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // получаем информацию о треке
        val arguments = arguments

        arguments?.getLong(PlaylistInfoFragment.ARGS_PLAYLIST_ID)?.let {
            playlistId = it
        }

        viewModel.getPlaylistFromDB(playlistId)

        viewModel.getPlaylistTracksLiveData().observe(owner) {
            playlistTracksList = it
            binding.minutes.text = getTotalMinutes()
        }

        viewModel.getPlaylistLiveData()
            .observe(owner) { playlist -> renderPlaylistInfo(playlist) }

        // bottom sheet
        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName
        binding.year.text = playlist.playlistInfo
        binding.tracksCount.text = makeText(playlist.tracksCount)
    }

    private fun getTotalMinutes(): String {
        var durationSum = 0
        for (track in playlistTracksList) {
            durationSum += track.trackTime
        }

        val sum = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)

        val chars = sum.toString().toList().reversed()
        var word = "минут"

        // число заканчивается на 0 или на 1 кроме 11
        val firstForm = chars[0] == '0' || (chars[0] == '1' && chars[1] != '1')

        // число заканчивается на 2, 3, 4 кроме 12, 13, 14
        val secondForm = (chars[0] == '2' || chars[0] == '3' || chars[0] == '4') && chars[1] != '1'

        if (firstForm) {
            word = "минута"
        } else if (secondForm) {
            word = "минуты"
        }
        return "${sum.toString().trimStart('0')} $word"
    }

    private fun makeText(tracksCount: Int): String {
        val num = tracksCount.toString()
        var word = "трек"
        when (num.last()) {
            '2', '3', '4' -> {
                word = "трека"
            }

            else -> word = "треков"
        }
        return "$num $word"
    }
}