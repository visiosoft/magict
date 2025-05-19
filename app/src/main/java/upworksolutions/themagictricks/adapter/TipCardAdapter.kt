package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.model.TipCard

class TipCardAdapter(
    private val onCardClick: (TipCard) -> Unit,
    private val onFavoriteClick: (TipCard) -> Unit
) : RecyclerView.Adapter<TipCardAdapter.TipCardViewHolder>() {

    private var cards: List<TipCard> = emptyList()

    fun submitList(newCards: List<TipCard>) {
        cards = newCards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip_card, parent, false)
        return TipCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipCardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    inner class TipCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val backgroundImage: ImageView = itemView.findViewById(R.id.backgroundImage)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        private val difficultyText: TextView = itemView.findViewById(R.id.difficultyText)
        private val secretTipText: TextView = itemView.findViewById(R.id.secretTipText)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

        fun bind(card: TipCard) {
            titleText.text = card.title
            descriptionText.text = card.description
            difficultyText.text = card.difficulty
            secretTipText.text = card.secretTip
            
            // Load background image with Coil
            backgroundImage.load(card.backgroundImageUrl) {
                crossfade(true)
            }

            // Set favorite button state
            favoriteButton.setImageResource(
                if (card.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            // Set click listeners
            itemView.setOnClickListener { onCardClick(card) }
            favoriteButton.setOnClickListener { onFavoriteClick(card) }

            // Add swipe up/down animation for secret tip
            itemView.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_UP -> {
                        if (event.historySize > 0) {
                            val deltaY = event.y - event.getHistoricalY(0)
                            if (Math.abs(deltaY) > 50) { // Threshold for swipe
                                val animation = if (deltaY < 0) {
                                    // Swipe up - reveal tip
                                    AnimationUtils.loadAnimation(view.context, R.anim.reveal_tip)
                                } else {
                                    // Swipe down - hide tip
                                    AnimationUtils.loadAnimation(view.context, R.anim.hide_tip)
                                }
                                secretTipText.startAnimation(animation)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
} 