package upworksolutions.themagictricks

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import upworksolutions.themagictricks.adapter.HorizontalCategoriesAdapter
import upworksolutions.themagictricks.adapter.VideoTrickAdapter
import upworksolutions.themagictricks.adapter.QuoteAdapter
import upworksolutions.themagictricks.model.Category
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.player.VideoPlayerHelper
import upworksolutions.themagictricks.activity.VideoPlayerActivity
import upworksolutions.themagictricks.util.AdMobConfig
import coil.load
import upworksolutions.themagictricks.data.TrickDataProvider
import androidx.media3.common.util.UnstableApi
import upworksolutions.themagictricks.util.HorizontalSpaceItemDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import upworksolutions.themagictricks.model.TipCard
import upworksolutions.themagictricks.adapter.TipCardAdapter
import upworksolutions.themagictricks.activity.TipDetailActivity
import upworksolutions.themagictricks.fragment.CategoryVideosFragment
import upworksolutions.themagictricks.util.AdManager
import android.util.Log
import androidx.lifecycle.lifecycleScope

@UnstableApi
class HomeActivity : AppCompatActivity() {
    
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var trendingRecyclerView: RecyclerView
    private lateinit var shortVideosRecyclerView: RecyclerView
    private lateinit var tipCardsViewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var adView: AdView
    private lateinit var adManager: AdManager
    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    
    private lateinit var categoriesAdapter: HorizontalCategoriesAdapter
    private lateinit var trendingAdapter: VideoTrickAdapter
    private lateinit var shortVideosAdapter: VideoTrickAdapter
    private lateinit var videoPlayerHelper: VideoPlayerHelper
    private lateinit var quotesRecyclerView: RecyclerView
    private val quotesAdapter: QuoteAdapter by lazy { QuoteAdapter() }
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize AdMob
        MobileAds.initialize(this) {
            Log.d("AdMob", "Initialized")
        }

        // Initialize AdManager using singleton instance
        adManager = AdManager.getInstance(this)
        
        // Load initial interstitial ad
        adManager.loadInterstitialAd()

        // Initialize VideoPlayerHelper
        videoPlayerHelper = VideoPlayerHelper.getInstance(this)

        // Initialize views
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView)
        shortVideosRecyclerView = findViewById(R.id.shortVideosRecyclerView)
        tipCardsViewPager = findViewById(R.id.tipCardsViewPager)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        adView = findViewById(R.id.adView)
        quotesRecyclerView = findViewById(R.id.quotesRecyclerView)

        // Load banner ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Load hero image with Coil
        findViewById<ImageView>(R.id.heroImage).load(R.drawable.hero_image) {
            crossfade(true)
            size(1080, 1920) // Load high resolution
        }

        // Setup Play Icon click
        findViewById<ImageView>(R.id.heroPlayIcon).setOnClickListener {
            showInterstitialAd {
                val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra("videoUrl", "https://raw.githubusercontent.com/visiosoft/videostreaming/main/3.mp4")
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

        // Setup quotes
        setupQuotes()

        // Setup tip cards
        setupTipCards()
    }

    private fun loadAppOpenAd() {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            this,
            AdMobConfig.getAppOpenAdUnitId(),
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    showAppOpenAd()
                }

                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    appOpenAd = null
                }
            }
        )
    }

    private fun showAppOpenAd() {
        if (isShowingAd) {
            return
        }

        appOpenAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                loadAppOpenAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAppOpenAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }

        appOpenAd?.show(this)
    }

    private fun showInterstitialAd(onAdClosed: () -> Unit) {
        adManager.showInterstitialAd(onAdClosed)
    }

    private fun setupAdapters() {
        // Categories adapter
        categoriesAdapter = HorizontalCategoriesAdapter { category ->
            // Show category videos fragment
            showCategoryVideos(category)
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

    private fun showCategoryVideos(category: Category) {
        val fragment = CategoryVideosFragment.newInstance(category)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerViews() {
        // Setup Trending Videos RecyclerView
        trendingRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingAdapter
            setHasFixedSize(true)
            addItemDecoration(HorizontalSpaceItemDecoration(8))
        }

        // Setup Categories RecyclerView
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
            setHasFixedSize(true)
            addItemDecoration(HorizontalSpaceItemDecoration(8))
        }

        // Setup Short Videos RecyclerView
        shortVideosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = shortVideosAdapter
            setHasFixedSize(true)
            addItemDecoration(HorizontalSpaceItemDecoration(8))
        }

        // Setup Quotes RecyclerView (no auto-scrolling)
        quotesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = quotesAdapter
            setHasFixedSize(true)
            addItemDecoration(HorizontalSpaceItemDecoration(8))
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

        // Load trending tricks from JSON
        loadTricks()
    }

    private fun loadTricks() {
        lifecycleScope.launch {
            try {
                val tricks = TrickDataProvider.getTrendingTricks(this@HomeActivity)
                // Update UI with tricks
                updateUI(tricks)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tricks", e)
                // Show error to user
            }
        }
    }

    private fun loadTricksForCategory(category: Category) {
        // TODO: Implement category filtering
    }

    private fun setupQuotes() {
        quotesRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = quotesAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)))
        }
    }

    private fun setupTipCards() {
        val tipCards = listOf(
            TipCard(
                id = "1",
                title = "Vanishing Coin",
                description = "Make objects disappear with this classic sleight of hand! Perfect for beginners, this trick will amaze your audience with its simplicity and effectiveness.",
                backgroundImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80",
                difficulty = "Beginner",
                secretTip = "Misdirection is key! Keep your audience's attention on your other hand while performing the vanish."
            ),
            TipCard(
                id = "2",
                title = "Card Levitation",
                description = "Defy gravity with this mesmerizing card trick! Learn to make cards float and dance in the air, creating an illusion that will leave your audience spellbound.",
                backgroundImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80",
                difficulty = "Intermediate",
                secretTip = "Practice the thumb palm technique until it becomes second nature. The smoother your movements, the more magical the effect."
            ),
            TipCard(
                id = "3",
                title = "Mind Reading",
                description = "Amaze your audience by predicting their thoughts! This psychological illusion combines subtle cues with clever techniques to create a truly mind-bending experience.",
                backgroundImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80",
                difficulty = "Advanced",
                secretTip = "Use psychological forcing techniques to guide their choices. The key is to make them feel like they have complete freedom while subtly influencing their decisions."
            )
        )

        val tipCardAdapter = TipCardAdapter(
            onCardClick = { card ->
                // Handle card click - show detailed view
                showInterstitialAd {
                    val intent = Intent(this, TipDetailActivity::class.java).apply {
                        putExtra("tip_card", card)
                    }
                    startActivity(intent)
                }
            },
            onFavoriteClick = { card ->
                // Handle favorite click
                Toast.makeText(this, "Added ${card.title} to favorites", Toast.LENGTH_SHORT).show()
            }
        )

        tipCardsViewPager.apply {
            adapter = tipCardAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            // Add page transformer for 3D effect
            setPageTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
                page.rotationY = position * -30
            }
        }

        tipCardAdapter.submitList(tipCards)
    }

    private fun updateUI(tricks: List<Trick>) {
        trendingAdapter.submitList(tricks)
        shortVideosAdapter.submitList(tricks)
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
        loadInitialData()
        if (!isShowingAd) {
            showAppOpenAd()
        }
    }

    override fun onDestroy() {
        adView.destroy()
        videoPlayerHelper.release()
        super.onDestroy()
    }
} 