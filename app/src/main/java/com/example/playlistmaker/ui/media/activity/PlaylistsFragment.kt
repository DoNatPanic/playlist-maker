package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.media.entity.Playlist
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class PlaylistsFragment : Fragment() {

    private val viewModel by activityViewModel<PlaylistsViewModel>()

    private lateinit var binding: FragmentPlaylistsBinding

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var owner = getViewLifecycleOwner()

        playlistAdapter = PlaylistAdapter { playlist -> viewModel.onPlaylistClicked(playlist) }

        viewModel.getOpenPlaylistInfoTrigger().observe(owner) { playlist ->
            openPlaylistInfoFragment(playlist)
        }

        binding.newPlaylistBtn.setOnClickListener {
            openCreatePlaylistFragment()
        }

        val recyclerView = binding.recyclerView

        viewModel.getResultLiveData()
            .observe(owner) { searchResult -> renderSearchResult(searchResult) }

        recyclerView.layoutManager = GridLayoutManager(
            activity,
            2
        ) //ориентация по умолчанию — вертикальная
        recyclerView.adapter = playlistAdapter
    }

    private fun renderSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.PlaylistContent -> {
                setMessage("")
                playlistAdapter.setItems(result.results)
                playlistAdapter.notifyDataSetChanged()
                binding.notFoundImage.isVisible = false
                binding.recyclerView.isVisible = true
            }

            SearchResult.NotFound -> {
                setMessage(getString(R.string.playlists_empty))
                playlistAdapter.setItems(listOf())
                playlistAdapter.notifyDataSetChanged()
                binding.notFoundImage.isVisible = true
                binding.recyclerView.isVisible = false
            }

            else -> {}
        }
    }

    private fun setMessage(text: String) {
        if (text.isNotEmpty()) {
            binding.placeholderMessage.isVisible = true
            binding.placeholderMessage.text = text
        } else {
            binding.placeholderMessage.isVisible = false
        }
    }

    // перейти на экран создания плейлиста
    private fun openCreatePlaylistFragment() {
        findNavController().navigate(
            R.id.action_mediaFragment_to_createPlaylistFragment,
            null
        )
    }

    // перейти на экран информации о плейлисте
    private fun openPlaylistInfoFragment(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_playlistInfoFragment,
            PlaylistInfoFragment.createArgs(playlist.playlistId!!)
        )
    }

    companion object {
        private const val POSTER_URL = "poster_url"

        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply { }
        }
    }
}