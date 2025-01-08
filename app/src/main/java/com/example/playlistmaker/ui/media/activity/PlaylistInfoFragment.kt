package com.example.playlistmaker.ui.media.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

    private lateinit var playlist: Playlist

    private var playlistTracksList: List<Track> = listOf()

    private val viewModel: PlaylistInfoViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var binding: FragmentPlaylistInfoBinding

    private var job: Job? = null

    private var progress: MaterialAlertDialogBuilder? = null

    private var progressDialog: AlertDialog? = null

    private var menuBottomSheetContainer: LinearLayout? = null

    private var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

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


        // tracks bottom sheet
        val tracksBottomSheetContainer = binding.tracksBottomSheet
        val tracksBottomSheetBehavior =
            BottomSheetBehavior.from(tracksBottomSheetContainer)
        tracksBottomSheetBehavior.peekHeight = calcBottomMenuHeight()

        // menu bottom sheet
        menuBottomSheetContainer = binding.menuBottomSheet
        val menuOverlay = binding.menuOverlay
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer!!).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        menuOverlay.isVisible = false
                    }

                    else -> {
                        menuOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        progress =
            activity?.let { MaterialAlertDialogBuilder(it, R.style.Theme_MyApp_Dialog_Alert) };

        // назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // получаем информацию о треке
        val arguments = arguments

        arguments?.getLong(ARGS_PLAYLIST_ID)?.let {
            playlistId = it
        }

        viewModel.getPlaylistFromDB(playlistId)

        viewModel.getPlaylistTracksLiveData().observe(owner) {
            playlistTracksList = it
            binding.minutes.text = getTotalMinutes()
            trackAdapter.setItems(it)
            binding.noTracksText.isVisible = playlistTracksList.isEmpty()
        }

        viewModel.getPlaylistLiveData()
            .observe(owner) { pl ->
                playlist = pl
                renderPlaylistInfo()
            }

        val onTrackClick: (Track) -> Unit = { track: Track -> openAudioPlayer(track) }
        val onDeleteTrackClick: (Track) -> Boolean =
            { track: Track ->
                showDeleteTrackDialog(playlistId, track)
            }
        trackAdapter = TrackAdapter(onTrackClick, onDeleteTrackClick)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = trackAdapter

        binding.share.setOnClickListener {
            sharePlaylist()
        }

        binding.menu.setOnClickListener {
            renderSmallPlaylistView()
            menuBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }


        binding.shareBtn.setOnClickListener {
            sharePlaylist()
        }

        binding.editBtn.setOnClickListener {
            openPlaylistEditPage(playlist)
        }

        binding.deleteBtn.setOnClickListener {
            showDeletePlaylistDialog()
        }
    }

    private fun calcBottomMenuHeight(): Int {
        binding.main.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);
        binding.pickerImage.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);
        binding.playlistName.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);
        binding.year.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);
        binding.minutes.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);
        binding.share.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED);

        val value1 = binding.pickerImage.measuredWidth + binding.playlistName.measuredHeight
        +binding.year.measuredHeight + binding.minutes.measuredHeight + binding.share.measuredHeight

        val value2 = resources.getDimensionPixelSize(R.dimen.playlist_info_name_margin_top)
        +resources.getDimensionPixelSize(R.dimen.playlist_info_text_margin_top)
        +resources.getDimensionPixelSize(R.dimen.playlist_info_text_margin_top)
        +resources.getDimensionPixelSize(R.dimen.playlist_info_buttons_margin_top)
        +resources.getDimensionPixelSize(R.dimen.playlist_info_bottom_menu_margin_top)

        return binding.main.measuredHeight - value1 - value2
    }

    // редактировать плейлист
    private fun openPlaylistEditPage(playlist: Playlist) {
        menuBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_createPlaylistFragment,
            CreatePlaylistFragment.createArgs(playlist)
        )
    }

    override fun onResume() {
        super.onResume()
        menuBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun sharePlaylist() {
        menuBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        if (playlistTracksList.isNotEmpty()) {
            viewModel.onShareButtonClicked(playlist, playlistTracksList)
        } else {
            Toast.makeText(
                activity,
                "В данном плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun renderSmallPlaylistView() {
        binding.playlistSmallView.playlistName.text = playlist.playlistName
        binding.playlistSmallView.tracksCount.text = makeText(playlist.tracksCount)
        val image: ImageView = binding.playlistSmallView.sourceImage

        val radius = resources.getDimensionPixelSize(R.dimen.album_image_radius)

        Glide.with(this)
            .load(playlist.playlistImgPath)
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(image)
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

    // удалить плейлист
    private fun showDeletePlaylistDialog(): Boolean {
        progress?.setTitle("Хотите удалить плейлист ${playlist.playlistName}?")
        progress?.setNeutralButton("Нет") { dialog, which ->
            // empty
        }
        progress?.setPositiveButton("Да") { dialog, which ->
            progressDialog?.dismiss()
            viewModel.onDeletePlaylistClicked(playlist, playlistTracksList)
            Toast.makeText(
                activity,
                "Плейлист ${playlist.playlistName} был удален",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack()
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

    private fun renderPlaylistInfo() {
        if (playlist.playlistImgPath != null) {
            Glide.with(this)
                .load(playlist.playlistImgPath)
                .placeholder(R.drawable.track_image_large)
                .error(R.drawable.track_image_large)
                .centerCrop()
                .into(binding.pickerImage)
        }

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