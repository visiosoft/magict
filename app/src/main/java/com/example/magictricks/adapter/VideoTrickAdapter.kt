package upworksolutions.themagictricks.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.databinding.ItemVideoTrickBinding
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.activity.VideoPlayerActivity
import androidx.media3.common.util.UnstableApi

@UnstableApi
class VideoTrickAdapter(
    private val videoPlayerHelper: VideoPlayerHelper,
    private val onTrickClick: (Trick) -> Unit
) : ListAdapter<Trick, VideoTrickAdapter.VideoViewHolder>(TrickDiffCallback()) {

    private var currentPlayingPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoTrickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding).also { holder ->
            holder.itemView.tag = holder
        }
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        if (holder.bindingAdapterPosition == currentPlayingPosition) {
            stopPlayback()
        }
        Glide.with(holder.itemView.context).clear(holder.binding.ivThumbnail)
    }

    fun checkVisibility(position: Int, visiblePercentage: Float) {
        when {
            visiblePercentage >= 0.7f && position != currentPlayingPosition -> {
                startPlayback(position)
            }
            visiblePercentage < 0.5f && position == currentPlayingPosition -> {
                stopPlayback()
            }
        }
    }

    private fun startPlayback(position: Int) {
        stopPlayback()
        currentPlayingPosition = position
        val trick = getItem(position)
        videoPlayerHelper.preparePlayer(android.net.Uri.parse(trick.videoUrl))
    }

    private fun stopPlayback() {
        if (currentPlayingPosition != -1) {
            videoPlayerHelper.release()
            currentPlayingPosition = -1
        }
    }

    inner class VideoViewHolder(val binding: ItemVideoTrickBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Set click listener for the Watch Now button
            binding.btnWatchNow.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val trick = getItem(position)
                    val context = itemView.context
                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("video_url", trick.videoUrl)
                        putExtra("title", trick.title)
                        putExtra("description", trick.description)
                    }
                    context.startActivity(intent)
                }
            }

            // Set click listener for the entire item
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val trick = getItem(position)
                    val context = itemView.context
                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("video_url", trick.videoUrl)
                        putExtra("title", trick.title)
                        putExtra("description", trick.description)
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(trick: Trick) {
            binding.apply {
                tvTitle.text = trick.title
                tvDuration.text = trick.getFormattedDuration()
                
                // Load thumbnail using Glide with loading state
                Glide.with(itemView.context)
                    .load(trick.thumbnailUrl)
                    .placeholder(R.drawable.loading_thumbnail)
                    .error(R.drawable.error_thumbnail)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivThumbnail)
            }
        }
    }

    private class TrickDiffCallback : DiffUtil.ItemCallback<Trick>() {
        override fun areItemsTheSame(oldItem: Trick, newItem: Trick): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trick, newItem: Trick): Boolean {
            return oldItem == newItem
        }
    }
} 