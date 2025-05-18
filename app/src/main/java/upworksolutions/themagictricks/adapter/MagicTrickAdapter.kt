package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.databinding.ItemRecommendedVideoBinding
import upworksolutions.themagictricks.model.Trick

class MagicTrickAdapter(
    private val onTrickClick: (Trick) -> Unit
) : ListAdapter<Trick, MagicTrickAdapter.TrickViewHolder>(TrickDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrickViewHolder {
        val binding = ItemRecommendedVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrickViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrickViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: TrickViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.ivThumbnail)
    }

    inner class TrickViewHolder(val binding: ItemRecommendedVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrickClick(getItem(position))
                }
            }
        }

        fun bind(trick: Trick) {
            binding.apply {
                tvTitle.text = trick.title
                tvDuration.text = trick.getFormattedDuration()

                // Load thumbnail using Glide with proper configuration
                Glide.with(ivThumbnail)
                    .load(trick.thumbnailUrl)
                    .placeholder(R.drawable.loading_thumbnail)
                    .error(R.drawable.error_thumbnail)
                    .centerCrop()
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