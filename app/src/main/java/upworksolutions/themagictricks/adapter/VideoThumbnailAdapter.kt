package upworksolutions.themagictricks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.model.Trick

class VideoThumbnailAdapter(
    private val tricks: List<Trick>,
    private val onItemClick: (Trick) -> Unit
) : RecyclerView.Adapter<VideoThumbnailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImage: ImageView = view.findViewById(R.id.thumbnailImage)
        val titleText: TextView = view.findViewById(R.id.titleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_thumbnail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trick = tricks[position]
        
        holder.titleText.text = trick.title

        // Load thumbnail using Glide
        Glide.with(holder.itemView.context)
            .load(trick.thumbnailUrl)
            .centerCrop()
            .into(holder.thumbnailImage)

        holder.itemView.setOnClickListener {
            onItemClick(trick)
        }
    }

    override fun getItemCount() = tricks.size
} 