package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import upworksolutions.themagictricks.databinding.ItemVideoThumbnailBinding
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.util.AdMobConfig
import upworksolutions.themagictricks.R

class VideoThumbnailAdapter(
    private val items: List<Any>,
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<VideoThumbnailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImageView: ImageView = view.findViewById(R.id.thumbnailImageView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val subtitleTextView: TextView = view.findViewById(R.id.subtitleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val itemsNeededLayout: LinearLayout = view.findViewById(R.id.itemsNeededLayout)
        val stepsLayout: LinearLayout = view.findViewById(R.id.stepsLayout)
        val howItWorksTextView: TextView = view.findViewById(R.id.howItWorksTextView)
        val difficultyTextView: TextView = view.findViewById(R.id.difficultyTextView)
        val difficultyIcon: ImageView = view.findViewById(R.id.difficultyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_thumbnail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item is Trick) {
            Glide.with(holder.thumbnailImageView.context)
                .load(item.thumbnailUrl)
                .into(holder.thumbnailImageView)
            holder.titleTextView.text = item.title
            holder.subtitleTextView.text = item.subtitle
            holder.descriptionTextView.text = item.description
            // Bullet points for items needed
            holder.itemsNeededLayout.removeViews(1, holder.itemsNeededLayout.childCount - 1)
            item.itemsNeeded.forEach { needed ->
                val tv = TextView(holder.itemsNeededLayout.context)
                tv.text = "• $needed"
                tv.setTextColor(0xFF222222.toInt())
                tv.textSize = 14f
                tv.setPadding(16, 0, 0, 0)
                holder.itemsNeededLayout.addView(tv)
            }
            // Bullet points for steps
            holder.stepsLayout.removeViews(1, holder.stepsLayout.childCount - 1)
            item.steps.forEachIndexed { idx, step ->
                val tv = TextView(holder.stepsLayout.context)
                tv.text = "${idx + 1}. $step"
                tv.setTextColor(0xFF333333.toInt())
                tv.textSize = 14f
                tv.setPadding(16, 0, 0, 0)
                holder.stepsLayout.addView(tv)
            }
            holder.howItWorksTextView.text = "How It Works: ${item.howItWorks}"
            holder.difficultyTextView.text = item.difficulty
            // Optionally, set icon tint or image based on difficulty
            holder.itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = items.size
} 