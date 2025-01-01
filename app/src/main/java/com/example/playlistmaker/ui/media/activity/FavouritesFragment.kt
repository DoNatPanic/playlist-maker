package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerFragment
import com.example.playlistmaker.ui.media.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FavouritesFragment : Fragment() {

    private val viewModel by activityViewModel<FavouritesViewModel>()

    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var owner = getViewLifecycleOwner()

        trackAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = trackAdapter

        viewModel.getResultLiveData()
            .observe(owner) { searchResult -> renderSearchResult(searchResult) }

        viewModel.getOpenMediaPlayerTrigger().observe(owner) { track ->
            openAudioPlayer(track)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onReload()
    }

    private fun renderSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.TrackContent -> {
                setMessage("")
                trackAdapter.setItems(result.results)
                trackAdapter.notifyDataSetChanged()
                binding.notFoundImage.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            SearchResult.NotFound -> {
                setMessage(getString(R.string.favourites_empty))
                trackAdapter.setItems(listOf())
                trackAdapter.notifyDataSetChanged()
                binding.notFoundImage.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

            else -> {}
        }
    }

    private fun setMessage(text: String) {
        if (text.isNotEmpty()) {
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.placeholderMessage.text = text
        } else {
            binding.placeholderMessage.visibility = View.GONE
        }
    }

    // перейти на экран аудиоплеера
    private fun openAudioPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    companion object {
        fun newInstance() = FavouritesFragment().apply {
            arguments = Bundle().apply { }
        }
    }
}