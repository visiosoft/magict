package upworksolutions.themagictricks

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import upworksolutions.themagictricks.adapter.HorizontalCategoriesAdapter
import upworksolutions.themagictricks.adapter.VideoTrickAdapter
import upworksolutions.themagictricks.model.Category
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.activity.VideoPlayerActivity
import upworksolutions.themagictricks.util.AdMobConfig
import coil.load
import upworksolutions.themagictricks.data.TrickDataProvider
import androidx.media3.common.util.UnstableApi

@UnstableApi
class HomeActivity : AppCompatActivity() {
    
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var shortVideosRecyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    
    private lateinit var categoriesAdapter: HorizontalCategoriesAdapter
    private lateinit var trendingAdapter: VideoTrickAdapter
    private lateinit var shortVideosAdapter: VideoTrickAdapter
    private lateinit var videoPlayerHelper: VideoPlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize AdMob
        MobileAds.initialize(this) { initializationStatus ->
            // Initialization complete
        }

        // Initialize VideoPlayerHelper
        videoPlayerHelper = VideoPlayerHelper.getInstance(this)

        // Initialize views
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView)
        shortVideosRecyclerView = findViewById(R.id.shortVideosRecyclerView)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        adView = findViewById(R.id.adView)

        // Load banner ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Load interstitial ad
        loadInterstitialAd()

        // Load hero image with Coil
        findViewById<ImageView>(R.id.heroImage).load("https://i.ibb.co/Q4Jw22m/magictricks-min.png") {
            crossfade(true)
        }

        // Setup Watch Now button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.watchNowButton).setOnClickListener {
            showInterstitialAd {
                val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra("videoUrl", "https://archive.org/serve/TikTok-7241210541224561946/7241210541224561946.ia.mp4")
                    putExtra("videoTitle", "Featured Magic Trick")
                }
                startActivity(intent)
            }
        }

        // Setup More buttons
        findViewById<TextView>(R.id.trendingMoreButton).setOnClickListener {
            showInterstitialAd {
                // TODO: Navigate to full trending list
                Toast.makeText(this, "View all trending tricks", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.forYouMoreButton).setOnClickListener {
            showInterstitialAd {
                // TODO: Navigate to full For You list
                Toast.makeText(this, "View all recommended tricks", Toast.LENGTH_SHORT).show()
            }
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

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            AdMobConfig.getInterstitialAdUnitId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    mInterstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd(onAdClosed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadInterstitialAd() // Load the next interstitial
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    mInterstitialAd = null
                    loadInterstitialAd() // Load the next interstitial
                    onAdClosed()
                }
            }
            mInterstitialAd?.show(this)
        } else {
            onAdClosed()
        }
    }

    private fun setupAdapters() {
        // Categories adapter
        categoriesAdapter = HorizontalCategoriesAdapter { category ->
            // Handle category selection
            loadTricksForCategory(category)
        }
        
        // Trending adapter
        trendingAdapter = VideoTrickAdapter(videoPlayerHelper) { trick ->
            // Click handling is now done in the adapter
        }
        
        // Short videos adapter
        shortVideosAdapter = VideoTrickAdapter(videoPlayerHelper) { trick ->
            // Click handling is now done in the adapter
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
                LinearLayoutManager.HORIZONTAL,
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

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
        loadInitialData()
    }

    override fun onDestroy() {
        adView.destroy()
        videoPlayerHelper.release()
        super.onDestroy()
    }
} 