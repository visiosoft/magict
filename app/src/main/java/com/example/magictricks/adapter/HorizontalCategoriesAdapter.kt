package upworksolutions.themagictricks.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.databinding.ItemCategoryChipBinding
import upworksolutions.themagictricks.model.Category

class HorizontalCategoriesAdapter(
    private val onCategorySelected: (Category) -> Unit
) : ListAdapter<Category, HorizontalCategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION
    private val colorEvaluator = ArgbEvaluator()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedCategory(category: Category) {
        val oldPosition = selectedPosition
        val newPosition = currentList.indexOf(category)
        
        if (oldPosition != newPosition) {
            selectedPosition = newPosition
            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition)
            }
            if (newPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(newPosition)
            }
        }
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryChipBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = getItem(position)
                    setSelectedCategory(category)
                    onCategorySelected(category)
                }
            }
        }

        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name
            binding.ivCategoryIcon.setImageResource(category.iconResId)

            // Animate color changes
            val context = binding.root.context
            val unselectedColor = ContextCompat.getColor(context, R.color.category_unselected)
            val selectedColor = ContextCompat.getColor(context, R.color.category_selected)
            
            animateColorChange(unselectedColor, selectedColor)
        }

        private fun animateColorChange(fromColor: Int, toColor: Int) {
            ValueAnimator.ofObject(colorEvaluator, fromColor, toColor).apply {
                duration = 200
                addUpdateListener { animator ->
                    binding.root.setCardBackgroundColor(animator.animatedValue as Int)
                }
                start()
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
} 