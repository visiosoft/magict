package upworksolutions.themagictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import upworksolutions.themagictricks.R
import upworksolutions.themagictricks.adapter.VideoThumbnailAdapter
import upworksolutions.themagictricks.databinding.FragmentExploreBinding
import upworksolutions.themagictricks.model.Trick
import android.util.Log
import upworksolutions.themagictricks.data.TrickDataProvider
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import upworksolutions.themagictricks.activity.VideoPlayerActivity

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupRecyclerView()
    }

    private fun setupViews() {
        // Removed text setup
    }

    private fun setupRecyclerView() {
        binding.videosRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        
        lifecycleScope.launch {
            try {
                val tricks = TrickDataProvider.getTrendingTricks(requireContext())
                
                if (tricks.isEmpty()) {
                    Toast.makeText(requireContext(), "No videos found", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Loaded ${tricks.size} videos", Toast.LENGTH_SHORT).show()
                }
                
                // Insert ad thumbnails every three videos
                val itemsWithAds = mutableListOf<Any>()
                tricks.forEachIndexed { index, trick ->
                    itemsWithAds.add(trick)
                    if ((index + 1) % 3 == 0) {
                        itemsWithAds.add("ad") // Placeholder for ad
                    }
                }
                
                binding.videosRecyclerView.adapter = VideoThumbnailAdapter(itemsWithAds) { item ->
                    if (item is Trick) {
                        // Open VideoPlayerActivity on click with video URL, title, and description
                        val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                        intent.putExtra("videoUrl", item.videoUrl)
                        intent.putExtra("title", item.title)
                        intent.putExtra("description", item.description)
                        startActivity(intent)
                    } else {
                        // Handle ad click
                        Toast.makeText(requireContext(), "Ad clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ExploreFragment", "Error loading videos: ${e.message}", e)
                Toast.makeText(requireContext(), "Error loading videos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 