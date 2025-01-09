package com.example.playlistmaker.ui.search.activity

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.domain.search.entity.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    private val binding: TrackViewBinding,
    private val onProductClick: (track: Track) -> Unit,
    private val onDeleteTrackClick: (track: Track) -> Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime)
        binding.artistName.text = model.artistName
        val image: ImageView = itemView.findViewById(R.id.sourceImage)

        val radius = itemView.resources.getDimensionPixelSize(R.dimen.album_image_radius)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_image)
            .error(R.drawable.track_image)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(image)

        binding.root.setOnClickListener { _ -> onProductClick(model) }
        binding.root.setOnLongClickListener { _ -> onDeleteTrackClick(model) }
    }
}