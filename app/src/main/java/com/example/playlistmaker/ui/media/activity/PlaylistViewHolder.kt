package com.example.playlistmaker.ui.media.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.media.entity.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.name)
    private val tracksCount: TextView = itemView.findViewById(R.id.tracks_count)
    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(playlist: Playlist) {
        title.text = playlist.playlistName

        val num = playlist.tracksCount.toString()
        var word = "трек"
        when (num.last()) {
            '2', '3', '4' -> {
                word = "трека"
            }

            else -> word = "треков"
        }
        tracksCount.text = "${playlist.tracksCount} $word"

        val radius = itemView.resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

        Glide.with(itemView)
            .load(playlist.playlistImgPath)
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(image)
    }
}