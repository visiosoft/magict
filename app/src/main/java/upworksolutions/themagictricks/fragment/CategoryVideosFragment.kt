package upworksolutions.themagictricks.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import upworksolutions.themagictricks.adapter.CategoryVideoAdapter
import upworksolutions.themagictricks.databinding.FragmentCategoryVideosBinding
import upworksolutions.themagictricks.model.Category
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.data.TrickDataProvider
import upworksolutions.themagictricks.activity.VideoPlayerActivity
import upworksolutions.themagictricks.util.AdManager
import upworksolutions.themagictricks.model.Trick

class CategoryVideosFragment : Fragment() {
    private var _binding: FragmentCategoryVideosBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var videoPlayerHelper: VideoPlayerHelper
    private lateinit var videoAdapter: CategoryVideoAdapter
    private lateinit var category: Category
    private lateinit var adManager: AdManager
    private var isReturningFromPlayer = false
    private val TAG = "CategoryVideosFragment"

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
        
        // Initialize AdManager using singleton instance
        adManager = AdManager.getInstance(requireContext())
        
        setupViews()
        loadCategoryTricks()
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
            if (!isReturningFromPlayer) {
                // Show ad only when user explicitly clicks on a video
                adManager.showInterstitialAd {
                    val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                        putExtra("videoUrl", trick.videoUrl)
                        putExtra("videoTitle", trick.title)
                    }
                    startActivity(intent)
                }
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadCategoryTricks() {
        lifecycleScope.launch {
            try {
                val tricks = TrickDataProvider.getTricksByCategory(requireContext(), category.name)
                // Update UI with category tricks
                updateCategoryTricks(tricks)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading category tricks", e)
            }
        }
    }

    private fun updateCategoryTricks(tricks: List<Trick>) {
        videoAdapter.submitList(tricks)
    }

    override fun onResume() {
        super.onResume()
        // Reset the returning flag when the fragment resumes
        isReturningFromPlayer = false
    }

    override fun onPause() {
        super.onPause()
        // Set the returning flag when the fragment is paused
        isReturningFromPlayer = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isReturningFromPlayer = false
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