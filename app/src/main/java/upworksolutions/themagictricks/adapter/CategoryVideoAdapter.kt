package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import upworksolutions.themagictricks.databinding.ItemCategoryVideoBinding
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.player.VideoPlayerHelper
import java.util.concurrent.TimeUnit

class CategoryVideoAdapter(
    private val videoPlayerHelper: VideoPlayerHelper,
    private val onVideoClick: (Trick) -> Unit
) : ListAdapter<Trick, CategoryVideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemCategoryVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VideoViewHolder(
        private val binding: ItemCategoryVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVideoClick(getItem(position))
                }
            }

            binding.watchButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onVideoClick(getItem(position))
                }
            }
        }

        fun bind(trick: Trick) {
            binding.apply {
                // Load thumbnail
                thumbnailImage.load(trick.thumbnailUrl) {
                    crossfade(true)
                }

                // Set title and description
                titleText.text = trick.title
                descriptionText.text = trick.description

                // Format duration
                val minutes = TimeUnit.SECONDS.toMinutes(trick.duration.toLong())
                val seconds = trick.duration % 60
                durationBadge.text = String.format("%d:%02d", minutes, seconds)

                // Set difficulty badge
                difficultyBadge.text = when {
                    trick.difficulty == "Beginner" -> "Beginner"
                    trick.difficulty == "Intermediate" -> "Intermediate"
                    else -> "Advanced"
                }

                // Show/hide pro badge
                proBadge.visibility = if (trick.isPro) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }
    }

    private class VideoDiffCallback : DiffUtil.ItemCallback<Trick>() {
        override fun areItemsTheSame(oldItem: Trick, newItem: Trick): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trick, newItem: Trick): Boolean {
            return oldItem == newItem
        }
    }
} 