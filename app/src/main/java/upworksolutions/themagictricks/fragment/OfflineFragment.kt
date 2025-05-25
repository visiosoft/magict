package upworksolutions.themagictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.adapter.VideoThumbnailAdapter
import upworksolutions.themagictricks.model.Trick
import android.util.Log
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import upworksolutions.themagictricks.activity.VideoPlayerActivity

class OfflineFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoThumbnailAdapter
    private val tricks = mutableListOf<Trick>()

    data class OfflineTricksResponse(val magic_tricks: List<OfflineTrick>)
    data class OfflineTrick(
        val id: Int,
        val title: String,
        val thumbnail: String,
        val subtitle: String,
        val description: String,
        val items_needed: List<String>,
        val steps: List<String>,
        val how_it_works: String,
        val difficulty: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = VideoThumbnailAdapter(tricks as List<Any>, { item ->
            if (item is Trick) {
                val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("videoUrl", item.videoUrl)
                intent.putExtra("title", item.title)
                intent.putExtra("description", item.description)
                intent.putExtra("subtitle", item.subtitle)
                intent.putExtra("itemsNeeded", item.itemsNeeded.toTypedArray())
                intent.putExtra("steps", item.steps.toTypedArray())
                intent.putExtra("howItWorks", item.howItWorks)
                intent.putExtra("difficulty", item.difficulty)
                startActivity(intent)
            }
        }, false)
        recyclerView.adapter = adapter
        loadOfflineTricks()
    }

    private fun loadOfflineTricks() {
        lifecycleScope.launch {
            try {
                val jsonString = requireContext().assets.open("offlinetricks.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<OfflineTricksResponse>() {}.type
                val response = Gson().fromJson<OfflineTricksResponse>(jsonString, type)
                tricks.clear()
                tricks.addAll(response.magic_tricks.map { offlineTrick ->
                    Trick(
                        id = offlineTrick.id.toString(),
                        title = offlineTrick.title ?: "",
                        description = offlineTrick.description ?: "",
                        videoUrl = "file:///android_asset/offline_videos/${offlineTrick.id}.mp4",
                        thumbnailUrl = offlineTrick.thumbnail ?: "",
                        duration = 0,
                        categories = emptyList(),
                        isPro = false,
                        isFeatured = false,
                        difficulty = offlineTrick.difficulty ?: "",
                        subtitle = offlineTrick.subtitle ?: "",
                        itemsNeeded = offlineTrick.items_needed ?: emptyList(),
                        steps = offlineTrick.steps ?: emptyList(),
                        howItWorks = offlineTrick.how_it_works ?: ""
                    )
                })
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("OfflineFragment", "Error loading offline tricks: ${e.message}", e)
                Toast.makeText(requireContext(), "Error loading offline tricks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 