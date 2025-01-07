package com.example.playlistmaker.ui.media.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerFragment
import com.example.playlistmaker.ui.media.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val ARGS_PLAYLIST_ID = "playlistId"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

    private var playlistId: Long = 0

    private var playlistTracksList: List<Track> = listOf()

    private val viewModel: PlaylistInfoViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var binding: FragmentPlaylistInfoBinding

    private var job: Job? = null

    var progress: MaterialAlertDialogBuilder? = null
    var progressDialog: AlertDialog? = null

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

        progress =
            activity?.let { MaterialAlertDialogBuilder(it, R.style.Theme_MyApp_Dialog_Alert) };

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
            trackAdapter.setItems(it)
        }

        viewModel.getPlaylistLiveData()
            .observe(owner) { playlist -> renderPlaylistInfo(playlist) }


        val onTrackClick: (Track) -> Unit = { track: Track -> openAudioPlayer(track) }
        val onDeleteTrackClick: (Track) -> Boolean =
            { track: Track ->
                showDeleteTrackDialog(playlistId, track)
            }
        trackAdapter = TrackAdapter(onTrackClick, onDeleteTrackClick)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = trackAdapter


        // bottom sheet
        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    // перейти на экран аудиоплеера
    private fun openAudioPlayer(track: Track) {
        clickDebounce()
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    // удалить трек
    private fun showDeleteTrackDialog(playlistId: Long, track: Track): Boolean {
        progress?.setTitle("Хотите удалить трек?")
        progress?.setNeutralButton("Нет") { dialog, which ->
            // empty
        }
        progress?.setPositiveButton("Да") { dialog, which ->
            progressDialog?.dismiss()
            viewModel.onDeleteTrackClicked(playlistId, track)
        }
        progress?.show()

        return true
    }

    private fun clickDebounce() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(PlaylistInfoFragment.CLICK_DEBOUNCE_DELAY)
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
        val firstForm = chars[0] == '1' && chars[1] != '1'

        // число заканчивается на 2, 3, 4 кроме 12, 13, 14
        val secondForm = (chars[0] == '2' || chars[0] == '3' || chars[0] == '4') && chars[1] != '1'

        if (firstForm) {
            word = "минута"
        } else if (secondForm) {
            word = "минуты"
        }
        if (sum.toString() == "00") return "0 $word"
        else
            return "${sum.toString().trimStart('0')} $word"
    }

    private fun makeText(tracksCount: Int): String {
        val num = tracksCount.toString()
        val chars = num.toList().reversed()
        var word = "трек"
        when (chars[0]) {
            '2', '3', '4' -> {
                word = "трека"
            }

            '0', '5', '6', '7', '8', '9' -> word = "треков"
        }
        if (chars.size > 1) {
            if ((chars[0] == '1' || chars[0] == '2' || chars[0] == '3' || chars[0] == '4') && chars[1] == '1') word =
                "треков"
        }

        return "$num $word"
    }
}