package com.example.magictricks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.magictricks.databinding.ItemMagicTrickBinding
import com.example.magictricks.model.Trick

interface TricksAdapterListener {
    fun onTrickClick(trick: Trick)
    fun onLikeClick(trick: Trick)
    fun onSaveClick(trick: Trick)
    fun onVideoPreloadComplete(trick: Trick)
}

class TricksAdapter(
    private val listener: TricksAdapterListener
) : ListAdapter<Trick, TricksAdapter.TrickViewHolder>(TrickDiffCallback()) {

    private var exoPlayer: ExoPlayer? = null
    private val preloadQueue = mutableListOf<Trick>()
    private var isPreloading = false
    private var context: android.content.Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrickViewHolder {
        context = parent.context
        val binding = ItemMagicTrickBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrickViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrickViewHolder, position: Int) {
        val trick = getItem(position)
        holder.bind(trick)
        
        // Add to preload queue if within next 3 items
        if (position < itemCount - 1 && position >= currentList.size - 4) {
            preloadQueue.add(trick)
            if (!isPreloading) {
                startPreloading()
            }
        }
    }

    override fun onViewRecycled(holder: TrickViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    private fun startPreloading() {
        if (preloadQueue.isEmpty()) {
            isPreloading = false
            return
        }

        isPreloading = true
        val trick = preloadQueue.removeFirst()
        
        if (exoPlayer == null && context != null) {
            exoPlayer = ExoPlayer.Builder(context!!).build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
            }
        }

        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(trick.videoUrl))
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        listener.onVideoPreloadComplete(trick)
                        stop()
                        clearMediaItems()
                        startPreloading()
                    }
                }
            })
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        preloadQueue.clear()
        isPreloading = false
    }

    inner class TrickViewHolder(
        private val binding: ItemMagicTrickBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTrickClick(getItem(position))
                }
            }
        }

        fun bind(trick: Trick) {
            binding.apply {
                tvTitle.text = trick.title
                tvDuration.text = trick.getFormattedDuration()

                // Load thumbnail using Glide with transition
                Glide.with(ivThumbnail)
                    .load(trick.thumbnailUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivThumbnail)
            }
        }

        fun recycle() {
            Glide.with(binding.ivThumbnail).clear(binding.ivThumbnail)
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