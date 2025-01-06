package com.example.playlistmaker.ui.media.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistSmallViewBinding
import com.example.playlistmaker.domain.media.entity.Playlist

class PlaylistSmallAdapter(
    private val onClick: (playlist: Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistSmallViewHolder>() {
    private var playlists: List<Playlist> = emptyList()

    fun setItems(items: List<Playlist>) {
        playlists = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSmallViewHolder {
        val binding = PlaylistSmallViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistSmallViewHolder(binding) { track ->
            onClick(track)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistSmallViewHolder, position: Int) {
        // holder.bind(playlists[position])
        // this line equals:
        playlists.getOrNull(position)?.let { track ->
            holder.bind(track)
        }
    }
}