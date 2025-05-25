package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_VIDEO = 0
        private const val TYPE_AD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Trick) TYPE_VIDEO else TYPE_AD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_VIDEO) {
            val binding = ItemVideoThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VideoViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_native_ad, parent, false)
            AdViewHolder(view as NativeAdView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            holder.bind(items[position] as Trick)
        } else if (holder is AdViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size

    inner class VideoViewHolder(private val binding: ItemVideoThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trick: Trick) {
            binding.tvTitle.text = trick.title
            binding.tvDescription.text = trick.description
            Glide.with(binding.root).load(trick.thumbnailUrl).into(binding.ivThumbnail)
            binding.root.setOnClickListener { onItemClick(trick) }
        }
    }

    inner class AdViewHolder(private val adView: NativeAdView) : RecyclerView.ViewHolder(adView) {
        fun bind() {
            val context = adView.context
            val adLoader = AdLoader.Builder(context, AdMobConfig.getNativeAdvancedAdUnitId())
                .forNativeAd { nativeAd: NativeAd ->
                    // Headline
                    (adView.findViewById<TextView>(R.id.ad_headline)).text = nativeAd.headline
                    adView.headlineView = adView.findViewById(R.id.ad_headline)
                    // Body
                    nativeAd.body?.let {
                        adView.findViewById<TextView>(R.id.ad_body).apply {
                            visibility = View.VISIBLE
                            text = it
                        }
                        adView.bodyView = adView.findViewById(R.id.ad_body)
                    } ?: run {
                        adView.findViewById<TextView>(R.id.ad_body).visibility = View.GONE
                    }
                    // Icon
                    nativeAd.icon?.drawable?.let {
                        adView.findViewById<ImageView>(R.id.ad_icon).apply {
                            visibility = View.VISIBLE
                            setImageDrawable(it)
                        }
                        adView.iconView = adView.findViewById(R.id.ad_icon)
                    } ?: run {
                        adView.findViewById<ImageView>(R.id.ad_icon).visibility = View.GONE
                    }
                    // Call to Action
                    nativeAd.callToAction?.let {
                        adView.findViewById<Button>(R.id.ad_call_to_action).apply {
                            visibility = View.VISIBLE
                            text = it
                        }
                        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                    } ?: run {
                        adView.findViewById<Button>(R.id.ad_call_to_action).visibility = View.GONE
                    }
                    adView.setNativeAd(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adView.visibility = View.GONE // Hide ad view if loading fails
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }
} 