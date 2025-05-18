package upworksolutions.themagictricks

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import upworksolutions.themagictricks.adapter.HorizontalCategoriesAdapter
import upworksolutions.themagictricks.adapter.VideoTrickAdapter
import upworksolutions.themagictricks.model.Category
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.activity.VideoPlayerActivity
import coil.load
import upworksolutions.themagictricks.data.TrickDataProvider

class HomeActivity : AppCompatActivity() {
    
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var shortVideosRecyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var categoriesAdapter: HorizontalCategoriesAdapter
    private lateinit var trendingAdapter: VideoTrickAdapter
    private lateinit var shortVideosAdapter: VideoTrickAdapter
    private lateinit var videoPlayerHelper: VideoPlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize VideoPlayerHelper
        videoPlayerHelper = VideoPlayerHelper.getInstance(this)

        // Initialize views
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView)
        shortVideosRecyclerView = findViewById(R.id.shortVideosRecyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load hero image with Coil
        findViewById<ImageView>(R.id.heroImage).load("https://i.ibb.co/Q4Jw22m/magictricks-min.png") {
            crossfade(true)
        }

        // Setup Watch Now button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.watchNowButton).setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            startActivity(intent)
        }

        // Setup adapters
        setupAdapters()
        
        // Setup RecyclerViews
        setupRecyclerViews()
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Load initial data
        loadInitialData()
    }

    private fun setupAdapters() {
        // Categories adapter
        categoriesAdapter = HorizontalCategoriesAdapter { category ->
            // Handle category selection
            loadTricksForCategory(category)
        }
        
        // Trending adapter
        trendingAdapter = VideoTrickAdapter(videoPlayerHelper) { trick ->
            // Navigate to VideoPlayerActivity
            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("video_url", trick.videoUrl)
                putExtra("title", trick.title)
                putExtra("description", trick.description)
            }
            startActivity(intent)
        }
        
        // Short videos adapter
        shortVideosAdapter = VideoTrickAdapter(videoPlayerHelper) { trick ->
            // Navigate to VideoPlayerActivity
            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("video_url", trick.videoUrl)
                putExtra("title", trick.title)
                putExtra("description", trick.description)
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerViews() {
        // Categories RecyclerView
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = categoriesAdapter
        }
        
        // Trending RecyclerView
        trendingRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = trendingAdapter
        }
        
        // Short Videos RecyclerView
        shortVideosRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = shortVideosAdapter
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home
                    true
                }
                R.id.navigation_explore -> {
                    // TODO: Navigate to explore
                    true
                }
                R.id.navigation_favorites -> {
                    // TODO: Navigate to favorites
                    true
                }
                R.id.navigation_practice -> {
                    // TODO: Navigate to practice
                    true
                }
                else -> false
            }
        }
    }

    private fun loadInitialData() {
        // Load sample categories
        val categories = listOf(
            Category("1", "Card Tricks", R.drawable.ic_card_trick),
            Category("2", "Coin Magic", R.drawable.ic_coin_trick),
            Category("3", "Rope Magic", R.drawable.ic_rope_trick),
            Category("4", "Mentalism", R.drawable.ic_mentalism)
        )
        categoriesAdapter.submitList(categories)

        // Load trending tricks from data provider
        val trendingTricks = TrickDataProvider.getTrendingTricks()
        trendingAdapter.submitList(trendingTricks)
        shortVideosAdapter.submitList(trendingTricks)
    }

    private fun loadTricksForCategory(category: Category) {
        // TODO: Implement category filtering
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayerHelper.release()
    }
} 