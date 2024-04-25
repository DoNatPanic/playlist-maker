package com.example.playlistmaker

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.InputStream
import java.net.URL


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val image: ImageView = itemView.findViewById(R.id.sourceImage)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView).load(model.artworkUrl100).into(image)
    }
}