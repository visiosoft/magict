package com.example.magictricks.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.magictricks.databinding.FragmentVideoPlayerBinding
import com.example.magictricks.model.Trick
import com.example.magictricks.player.VideoPlayerHelper

@UnstableApi
class VideoPlayerFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var videoPlayerHelper: VideoPlayerHelper
    private var trick: Trick? = null
    private val TAG = "VideoPlayerFragment"
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trick = it.getParcelable(ARG_TRICK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (trick == null) {
            Log.e(TAG, "Trick is null")
            Toast.makeText(context, "Error: No video data available", Toast.LENGTH_LONG).show()
            return
        }

        setupUI(trick!!)
        setupVideoPlayer()
        
        // Setup back button
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupUI(trick: Trick) {
        binding.apply {
            toolbar.title = trick.title
            tvTitle.text = trick.title
            tvDescription.text = trick.description
        }
    }

    private fun setupVideoPlayer() {
        try {
            // Use the trick's video URL if available, otherwise fall back to the sample URL
            val videoUrl = trick?.videoUrl ?: "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
            Log.d(TAG, "Setting up video player with URL: $videoUrl")

            // Initialize the player
            player = ExoPlayer.Builder(requireContext()).build()
            binding.playerView.player = player

            // Prepare the player with the video URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up video player", e)
            Toast.makeText(requireContext(), "Error playing video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        _binding = null
    }

    companion object {
        private const val ARG_TRICK = "trick"

        fun newInstance(trick: Trick) = VideoPlayerFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_TRICK, trick)
            }
        }
    }
} 