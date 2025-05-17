package com.example.magictricks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trick = it.getParcelable(ARG_TRICK, Trick::class.java)
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
        
        videoPlayerHelper = VideoPlayerHelper.getInstance(requireContext())
        setupPlayer()
        setupUI()
        setupBackPress()
    }
    
    private fun setupPlayer() {
        val player = videoPlayerHelper.createPlayer(requireContext())
        binding.playerView.player = player
        
        trick?.let { trick ->
            videoPlayerHelper.preparePlayer(android.net.Uri.parse(trick.videoUrl))
            player.playWhenReady = true
        }
    }
    
    private fun setupUI() {
        trick?.let { trick ->
            binding.tvTitle.text = trick.title
            binding.tvDescription.text = trick.description
        }
        
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
        private const val ARG_TRICK = "arg_trick"
        
        fun newInstance(trick: Trick) = VideoPlayerFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_TRICK, trick)
            }
        }
    }
} 