package com.example.magictricks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magictricks.databinding.ItemRecommendedVideoBinding
import com.example.magictricks.model.VideoItem

class RecommendedVideosAdapter(
    private val videos: List<VideoItem>,
    private val onVideoClick: (VideoItem) -> Unit
) : RecyclerView.Adapter<RecommendedVideosAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(
        private val binding: ItemRecommendedVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: VideoItem) {
            binding.titleTextView.text = video.title
            binding.durationTextView.text = video.duration

            // Load thumbnail using Glide
            Glide.with(binding.root.context)
                .load(video.thumbnailUrl)
                .centerCrop()
                .into(binding.thumbnailImageView)

            // Set click listener
            binding.root.setOnClickListener {
                onVideoClick(video)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemRecommendedVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size
} 