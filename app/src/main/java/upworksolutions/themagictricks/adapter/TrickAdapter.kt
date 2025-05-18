package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import upworksolutions.themagictricks.databinding.ItemMagicTrickBinding
import upworksolutions.themagictricks.model.Trick

class TrickAdapter(
    private val onItemClick: (Trick) -> Unit,
    private val onLikeClick: (Trick) -> Unit,
    private val onSaveClick: (Trick) -> Unit
) : ListAdapter<Trick, TrickAdapter.TrickViewHolder>(TrickDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrickViewHolder {
        val binding = ItemMagicTrickBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrickViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrickViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrickViewHolder(
        private val binding: ItemMagicTrickBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(trick: Trick) {
            binding.apply {
                tvTitle.text = trick.title
                tvDuration.text = trick.getFormattedDuration()

                // Load thumbnail using Glide
                Glide.with(ivThumbnail)
                    .load(trick.thumbnailUrl)
                    .centerCrop()
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