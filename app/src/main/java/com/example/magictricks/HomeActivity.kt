package com.example.magictricks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.magictricks.adapter.HorizontalCategoriesAdapter
import com.example.magictricks.adapter.VideoTrickAdapter
import com.example.magictricks.model.Category
import com.example.magictricks.model.Trick
import com.example.magictricks.player.VideoPlayerHelper
import com.example.magictricks.fragment.VideoPlayerFragment
import com.example.magictricks.fragment.WatchNowFragment

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

        // Setup Watch Now button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.watchNowButton).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, WatchNowFragment.newInstance())
                .addToBackStack(null)
                .commit()
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
            // Navigate to VideoPlayerFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, VideoPlayerFragment.newInstance(trick))
                .addToBackStack(null)
                .commit()
        }
        
        // Short videos adapter
        shortVideosAdapter = VideoTrickAdapter(videoPlayerHelper) { trick ->
            // Navigate to VideoPlayerFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, VideoPlayerFragment.newInstance(trick))
                .addToBackStack(null)
                .commit()
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

        // Load sample trending tricks
        val trendingTricks = listOf(
            Trick(
                id = "1",
                title = "Amazing Card Vanish",
                description = "Learn this impressive card vanish trick that will leave your audience amazed. Perfect for beginners!",
                videoUrl = "https://example.com/videos/card-vanish.mp4",
                thumbnailUrl = "https://example.com/thumbnails/card-vanish.jpg",
                duration = 180,
                categories = listOf("Card Tricks"),
                isPro = false,
                isFeatured = true
            ),
            Trick(
                id = "2",
                title = "Mind-Blowing Coin Magic",
                description = "Master the art of coin manipulation with this advanced technique. A must-learn for coin magic enthusiasts.",
                videoUrl = "https://example.com/videos/coin-magic.mp4",
                thumbnailUrl = "https://example.com/thumbnails/coin-magic.jpg",
                duration = 240,
                categories = listOf("Coin Magic"),
                isPro = true,
                isFeatured = true
            )
        )
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