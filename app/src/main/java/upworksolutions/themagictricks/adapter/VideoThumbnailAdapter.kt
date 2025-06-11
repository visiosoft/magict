package upworksolutions.themagictricks.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.TextUtils
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import upworksolutions.themagictricks.databinding.ItemVideoThumbnailBinding
import upworksolutions.themagictricks.databinding.ItemVideoThumbnailExploreBinding
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.util.AdMobConfig
import upworksolutions.themagictricks.R
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.util.Log

class VideoThumbnailAdapter(
    private val items: List<Any>,
    private val onItemClick: (Any) -> Unit,
    private val isExplore: Boolean = false
) : RecyclerView.Adapter<VideoThumbnailAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(0, 20)
    }

    private var adLoader: AdLoader? = null

    class ViewHolder(val binding: ItemVideoThumbnailExploreBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoThumbnailExploreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        
        // Initialize AdLoader if not already initialized
        if (adLoader == null) {
            adLoader = AdLoader.Builder(parent.context, AdMobConfig.getNativeAdvancedAdUnitId())
                .forNativeAd { nativeAd ->
                    // Find the position for this ad
                    val position = items.indexOfFirst { it is String && it == "ad" }
                    if (position != -1) {
                        // Replace the placeholder with the actual ad
                        (items as MutableList)[position] = nativeAd
                        notifyItemChanged(position)
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("VideoThumbnailAdapter", "Native ad failed to load: ${loadAdError.message}")
                    }
                })
                .build()
        }
        
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        if (item is Trick) {
            // Load thumbnail with Glide optimizations
            Glide.with(holder.itemView.context)
                .load(item.thumbnailUrl)
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.thumbnailImageView)

            // Set title
            holder.binding.titleTextView.text = item.title
            
            // Set description based on fragment type
            if (isExplore) {
                holder.binding.descriptionTextView.text = item.description
                holder.binding.descriptionTextView.maxLines = 2
                holder.binding.descriptionTextView.ellipsize = TextUtils.TruncateAt.END
                
                // Show play icon and make clickable only in explore mode
                holder.binding.playIconImageView.visibility = View.VISIBLE
                holder.itemView.setOnClickListener {
                    onItemClick(item)
                }
            } else {
                // For offline fragment, show full details with formatted sections
                val spannableString = SpannableStringBuilder()

                // Description section
                spannableString.append("Description:\n")
                spannableString.append(item.description)
                spannableString.append("\n\n")

                // How It Works section
                spannableString.append("How It Works:\n")
                spannableString.append(item.howItWorks)
                spannableString.append("\n\n")

                // Steps section
                spannableString.append("Steps:\n")
                item.steps.forEachIndexed { index, step ->
                    spannableString.append("${index + 1}. $step\n")
                }

                // Apply styles
                val text = spannableString.toString()
                val styledText = SpannableString(text)

                // Style section headers
                val headerStyle = StyleSpan(Typeface.BOLD)
                val headerColor = ForegroundColorSpan(Color.parseColor("#1A1A1A"))
                val contentColor = ForegroundColorSpan(Color.parseColor("#333333"))

                // Apply styles to all text first
                styledText.setSpan(contentColor, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                // Then apply header styles
                text.split("\n").forEachIndexed { _, line ->
                    if (line.endsWith(":")) {
                        val start = text.indexOf(line)
                        val end = start + line.length
                        styledText.setSpan(headerStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        styledText.setSpan(headerColor, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }

                holder.binding.descriptionTextView.text = styledText
                holder.binding.descriptionTextView.maxLines = Int.MAX_VALUE
                holder.binding.descriptionTextView.ellipsize = null
                
                // Hide play icon and make non-clickable in offline mode
                holder.binding.playIconImageView.visibility = View.GONE
                holder.itemView.setOnClickListener(null)
            }
        } else if (item is NativeAd) {
            // Handle native ad
            holder.binding.thumbnailImageView.setImageDrawable(null)
            holder.binding.titleTextView.text = item.headline
            holder.binding.descriptionTextView.text = item.body
            
            // Load ad icon if available
            item.icon?.drawable?.let { drawable ->
                holder.binding.thumbnailImageView.setImageDrawable(drawable)
            }
            
            // Hide play icon for ads
            holder.binding.playIconImageView.visibility = View.GONE
            
            // Set click listener for the entire ad view
            holder.itemView.setOnClickListener {
                // Show loading indicator
                holder.binding.titleTextView.text = "Opening..."
                holder.binding.descriptionTextView.text = "Please wait while we open the advertisement"
                
                // Handle ad click
                item.callToAction?.let { callToAction ->
                    try {
                        // Log the ad click
                        Log.d("VideoThumbnailAdapter", "Ad clicked: ${item.headline}")
                        
                        // Trigger the ad click
                        onItemClick(callToAction)
                    } catch (e: Exception) {
                        // Handle any errors
                        Log.e("VideoThumbnailAdapter", "Error handling ad click: ${e.message}")
                        
                        // Reset the ad view
                        holder.binding.titleTextView.text = item.headline
                        holder.binding.descriptionTextView.text = item.body
                    }
                }
            }
        } else if (item is String && item == "ad") {
            // Load a new native ad
            val adRequest = AdRequest.Builder().build()
            adLoader?.loadAd(adRequest)
            
            // Show placeholder with clear ad indication
            holder.binding.thumbnailImageView.setImageResource(R.drawable.ad_placeholder)
            holder.binding.titleTextView.text = "Advertisement"
            holder.binding.descriptionTextView.text = "Loading sponsored content..."
            
            // Ensure play icon is hidden for ad placeholder
            holder.binding.playIconImageView.visibility = View.GONE
            
            // Remove gradient overlay for ad placeholder
            holder.binding.root.findViewById<View>(R.id.gradientOverlay)?.visibility = View.GONE
            
            // Make placeholder non-clickable
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // Clear Glide load when view is recycled
        Glide.with(holder.itemView.context).clear(holder.binding.thumbnailImageView)
    }

    override fun getItemCount() = items.size
} 