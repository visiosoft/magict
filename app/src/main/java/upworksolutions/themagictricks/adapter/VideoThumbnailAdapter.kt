package upworksolutions.themagictricks.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TRICK = 0
        private const val VIEW_TYPE_AD = 1
    }

    class TrickViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImageView: ImageView = view.findViewById(R.id.thumbnailImageView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val subtitleTextView: TextView = view.findViewById(R.id.subtitleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val itemsNeededLayout: LinearLayout = view.findViewById(R.id.itemsNeededLayout)
        val stepsLayout: LinearLayout = view.findViewById(R.id.stepsLayout)
        val howItWorksTextView: TextView = view.findViewById(R.id.howItWorksTextView)
        val difficultyTextView: TextView = view.findViewById(R.id.difficultyTextView)
        val difficultyIcon: ImageView = view.findViewById(R.id.difficultyIcon)
        val shareIcon: ImageView = view.findViewById(R.id.shareIcon)
    }

    class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nativeAdView: NativeAdView = view.findViewById(R.id.native_ad_view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_TRICK else VIEW_TYPE_AD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRICK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_thumbnail, parent, false)
                TrickViewHolder(view)
            }
            VIEW_TYPE_AD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_native_ad_placeholder, parent, false)
                AdViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrickViewHolder -> {
                val item = items[position / 2]
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
                    // Remove item click
                    holder.itemView.setOnClickListener(null)
                    // Share icon click
                    holder.shareIcon.setOnClickListener {
                        val context = holder.itemView.context
                        val shareText = buildString {
                            append("${item.title}\n")
                            if (item.subtitle.isNotBlank()) append("${item.subtitle}\n")
                            if (item.description.isNotBlank()) append("${item.description}\n")
                            if (item.itemsNeeded.isNotEmpty()) append("Items Needed: ${item.itemsNeeded.joinToString(", ")}\n")
                            if (item.steps.isNotEmpty()) append("Steps:\n${item.steps.mapIndexed { i, s -> "${i+1}. $s" }.joinToString("\n")}\n")
                            if (item.howItWorks.isNotBlank()) append("How It Works: ${item.howItWorks}\n")
                            if (item.difficulty.isNotBlank()) append("Difficulty: ${item.difficulty}\n")
                        }
                        // Copy to clipboard
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Magic Trick", shareText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        // Share intent
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                        context.startActivity(Intent.createChooser(shareIntent, "Share Trick"))
                    }
                }
            }
            is AdViewHolder -> {
                // Load real native ad
                val adLoader = AdLoader.Builder(holder.itemView.context, AdMobConfig.getNativeAdvancedAdUnitId())
                    .forNativeAd { nativeAd: NativeAd ->
                        // Populate the native ad view
                        val adView = holder.nativeAdView
                        adView.headlineView = adView.findViewById(R.id.ad_headline)
                        adView.bodyView = adView.findViewById(R.id.ad_body)
                        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                        adView.iconView = adView.findViewById(R.id.ad_icon)

                        // Set the headline
                        (adView.headlineView as TextView).text = nativeAd.headline
                        adView.headlineView?.visibility = View.VISIBLE

                        // Set the body
                        if (nativeAd.body == null) {
                            adView.bodyView?.visibility = View.INVISIBLE
                        } else {
                            adView.bodyView?.visibility = View.VISIBLE
                            (adView.bodyView as TextView).text = nativeAd.body
                        }

                        // Set the call to action
                        if (nativeAd.callToAction == null) {
                            adView.callToActionView?.visibility = View.INVISIBLE
                        } else {
                            adView.callToActionView?.visibility = View.VISIBLE
                            (adView.callToActionView as Button).text = nativeAd.callToAction
                        }

                        // Set the icon
                        if (nativeAd.icon == null) {
                            adView.iconView?.visibility = View.GONE
                        } else {
                            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
                            adView.iconView?.visibility = View.VISIBLE
                        }

                        // Set the native ad
                        adView.setNativeAd(nativeAd)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            // Handle ad load failure
                            Toast.makeText(holder.itemView.context, "Ad failed to load: ${loadAdError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size * 2
    }
} 