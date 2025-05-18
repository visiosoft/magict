package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.model.MagicQuote

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {
    
    private val quotes = listOf(
        MagicQuote(
            "Magic is not about the trick, it's about the experience you create for your audience.",
            "Amit Kalantri"
        ),
        MagicQuote(
            "The best magic is the kind that makes people believe in the impossible.",
            "Misty Lee"
        ),
        MagicQuote(
            "Magic is the art of creating wonder and amazement through skill and misdirection.",
            "Amit Kalantri"
        ),
        MagicQuote(
            "A magician's greatest tool is not their hands, but their ability to tell a story.",
            "Misty Lee"
        ),
        MagicQuote(
            "The real magic happens when you make someone's day a little more magical.",
            "Amit Kalantri"
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote_card, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        holder.bind(quotes[position])
    }

    override fun getItemCount() = quotes.size

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quoteText: TextView = itemView.findViewById(R.id.quoteText)
        private val authorText: TextView = itemView.findViewById(R.id.authorText)

        fun bind(quote: MagicQuote) {
            quoteText.text = quote.quote
            authorText.text = "- ${quote.author}"
        }
    }
} 