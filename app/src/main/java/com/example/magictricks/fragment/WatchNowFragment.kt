package com.example.magictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import com.example.magictricks.databinding.FragmentVideoPlayerBinding
import com.example.magictricks.player.VideoPlayerHelper

@UnstableApi
class WatchNowFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoPlayerHelper: VideoPlayerHelper

    // You can set your featured/default video URL here
    private val featuredVideoUrl = "https://example.com/videos/card-vanish.mp4"
    private val featuredTitle = "Featured Magic Trick"
    private val featuredDescription = "Watch this amazing featured trick!"

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
        videoPlayerHelper = VideoPlayerHelper.getInstance(requireContext())
        setupPlayer()
        setupUI()
        setupBackPress()
    }

    private fun setupPlayer() {
        val player = videoPlayerHelper.createPlayer(requireContext())
        binding.playerView.player = player
        videoPlayerHelper.preparePlayer(android.net.Uri.parse(featuredVideoUrl))
        player.playWhenReady = true
    }

    private fun setupUI() {
        binding.tvTitle.text = featuredTitle
        binding.tvDescription.text = featuredDescription
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoPlayerHelper.release()
        _binding = null
    }

    companion object {
        fun newInstance() = WatchNowFragment()
    }
} 