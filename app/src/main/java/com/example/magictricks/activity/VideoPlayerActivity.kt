package com.example.magictricks.activity

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
import com.example.magictricks.databinding.ActivityVideoPlayerBinding
import com.example.magictricks.adapter.RecommendedVideosAdapter
import com.example.magictricks.model.VideoItem

@UnstableApi
class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var recommendedVideosAdapter: RecommendedVideosAdapter
    private val TAG = "VideoPlayerActivity"

    // Sample video data - replace with your actual data source
    private val recommendedVideos = listOf(
        VideoItem(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "Big Buck Bunny",
            "10:00",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        ),
        VideoItem(
            "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3",
            "Jazz in Paris",
            "5:30",
            "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
        ),
        VideoItem(
            "https://storage.googleapis.com/exoplayer-test-media-0/Sintel_360p.mp4",
            "Sintel",
            "15:45",
            "https://storage.googleapis.com/exoplayer-test-media-0/Sintel_360p.mp4"
        )
    )

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
    }

    private fun setupUI() {
        // Set title and description
        binding.tvTitle.text = "Amazing Magic Trick"
        binding.tvDescription.text = "Learn this incredible magic trick that will amaze your friends and family. This tutorial breaks down the technique step by step."

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
        try {
            recommendedVideosAdapter = RecommendedVideosAdapter(recommendedVideos) { videoItem ->
                // Handle video item click
                player.stop()
                val mediaItem = MediaItem.fromUri(videoItem.videoUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            }

            binding.recommendedVideosRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@VideoPlayerActivity)
                adapter = recommendedVideosAdapter
                setHasFixedSize(true)
                visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up recommended videos", e)
            Toast.makeText(this, "Error loading recommended videos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
} 