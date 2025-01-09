package com.example.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.entity.Track

class TrackAdapter(
    private val onProductClick: (track: Track) -> Unit,
    private val onDeleteTrackClick: (track: Track) -> Boolean = { _ -> false }
) : RecyclerView.Adapter<TrackViewHolder>() {
    private var tracks: List<Track> = emptyList()

    fun setItems(items: List<Track>) {
        tracks = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackViewHolder {
        val binding = TrackViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, onProductClick, onDeleteTrackClick)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        tracks.getOrNull(position)?.let { track ->
            holder.bind(track)
        }
    }
}

