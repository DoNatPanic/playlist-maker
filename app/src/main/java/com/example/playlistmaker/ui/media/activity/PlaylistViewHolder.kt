package com.example.playlistmaker.ui.media.activity

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.domain.media.entity.Playlist

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding,
    private val onClick: (playlist: Playlist) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {

        binding.name.text = playlist.playlistName
        binding.tracksCount.text = "${playlist.tracksCount} ${makeText(playlist.tracksCount)}"
        val image: ImageView = binding.image

        val radius = itemView.resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

        Glide.with(itemView)
            .load(playlist.playlistImgPath)
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(image)

        binding.root.setOnClickListener { _ -> onClick(playlist) }
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
        return word
    }
}