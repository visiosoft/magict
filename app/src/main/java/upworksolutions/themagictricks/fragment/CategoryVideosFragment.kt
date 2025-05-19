package upworksolutions.themagictricks.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import upworksolutions.themagictricks.adapter.CategoryVideoAdapter
import upworksolutions.themagictricks.databinding.FragmentCategoryVideosBinding
import upworksolutions.themagictricks.model.Category
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.data.TrickDataProvider
import upworksolutions.themagictricks.activity.VideoPlayerActivity

class CategoryVideosFragment : Fragment() {
    private var _binding: FragmentCategoryVideosBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var videoPlayerHelper: VideoPlayerHelper
    private lateinit var videoAdapter: CategoryVideoAdapter
    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getParcelable(ARG_CATEGORY)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadVideos()
    }

    private fun setupViews() {
        // Initialize VideoPlayerHelper
        videoPlayerHelper = VideoPlayerHelper.getInstance(requireContext())

        // Setup toolbar
        binding.toolbar.title = category.name
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // Setup RecyclerView
        videoAdapter = CategoryVideoAdapter(videoPlayerHelper) { trick ->
            // Launch video player activity
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra("videoUrl", trick.videoUrl)
                putExtra("videoTitle", trick.title)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadVideos() {
        val allTricks = TrickDataProvider.getTrendingTricks(requireContext())
        val filteredTricks = allTricks.filter { it.categories.contains(category.name) }
        videoAdapter.submitList(filteredTricks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: Category) = CategoryVideosFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_CATEGORY, category)
            }
        }
    }
} 