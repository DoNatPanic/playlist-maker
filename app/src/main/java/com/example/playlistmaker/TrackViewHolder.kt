package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val image: ImageView = itemView.findViewById(R.id.sourceImage)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)

        val radius = itemView.resources.getDimensionPixelSize(R.dimen.album_image_radius)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(image)
    }
}