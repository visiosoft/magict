package upworksolutions.themagictricks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import upworksolutions.themagictricks.databinding.ActivityVideoPlayerBinding
import upworksolutions.themagictricks.adapter.MagicTrickAdapter
import upworksolutions.themagictricks.data.TrickDataProvider
import upworksolutions.themagictricks.model.Trick

@UnstableApi
class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var recommendedVideosAdapter: MagicTrickAdapter
    private val TAG = "VideoPlayerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set window flags for full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Hide system bars
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupVideoPlayer()
        setupRecommendedVideos()
        setupUI()

        // Back button functionality
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        // Get title and description from intent
        val title = intent.getStringExtra("title") ?: "Amazing Magic Trick"
        val description = intent.getStringExtra("description") ?: "Learn this incredible magic trick that will amaze your friends and family. This tutorial breaks down the technique step by step."

        // Set title and description
        binding.tvDescription.text = description
        binding.tvTopTitle.text = title

        // Setup share button
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out this amazing magic trick!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Setup like button
        binding.btnLike.setOnClickListener {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }

        // Setup bookmark button
        binding.btnBookmark.setOnClickListener {
            Toast.makeText(this, "Saved for later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupVideoPlayer() {
        try {
            // Get video URL from intent or use default
            val videoUrl = intent.getStringExtra("video_url") 
                ?: "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
            
            Log.d(TAG, "Setting up video player with URL: $videoUrl")

            player = ExoPlayer.Builder(this).build()
            binding.playerView.player = player

            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up video player", e)
            Toast.makeText(this, "Error playing video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecommendedVideos() {
        recommendedVideosAdapter = MagicTrickAdapter { trick ->
            playVideo(trick.videoUrl)
        }
        binding.recommendedVideosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoPlayerActivity)
            adapter = recommendedVideosAdapter
        }
        recommendedVideosAdapter.submitList(TrickDataProvider.getTrendingTricks())
    }

    private fun playVideo(videoUrl: String) {
        player.stop()
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        // Find the trick that matches this video URL
        val trick = TrickDataProvider.getTrendingTricks().find { it.videoUrl == videoUrl }
        trick?.let {
            binding.tvTopTitle.text = it.title
            binding.tvDescription.text = it.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
} 