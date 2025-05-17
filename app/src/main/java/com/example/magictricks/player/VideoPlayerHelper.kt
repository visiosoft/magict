package com.example.magictricks.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.common.util.UnstableApi
import java.io.File

@UnstableApi
class VideoPlayerHelper private constructor(context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private val cache: SimpleCache
    private val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
    private val trackSelector: DefaultTrackSelector
    private val dataSourceFactory: CacheDataSource.Factory

    init {
        // Initialize cache
        val cacheDir = File(context.cacheDir, "media")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
        cache = SimpleCache(cacheDir, cacheEvictor)

        // Initialize track selector with adaptive bitrate
        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        trackSelector = DefaultTrackSelector(context, adaptiveTrackSelectionFactory)

        // Initialize data source factory with caching
        val upstreamFactory = DefaultDataSource.Factory(
            context,
            DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setUserAgent("MagicTricks/1.0")
        )

        dataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun createPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setBandwidthMeter(bandwidthMeter)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
                addListener(playerListener)
            }
    }

    fun preparePlayer(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        cache.release()
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_READY -> {
                    // Player is ready to play
                }
                Player.STATE_ENDED -> {
                    // Playback ended
                }
                Player.STATE_BUFFERING -> {
                    // Player is buffering
                }
                Player.STATE_IDLE -> {
                    // Player is idle
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            // Handle playback errors
        }
    }

    companion object {
        private const val MAX_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
        private var instance: VideoPlayerHelper? = null

        @Synchronized
        fun getInstance(context: Context): VideoPlayerHelper {
            return instance ?: VideoPlayerHelper(context.applicationContext).also {
                instance = it
            }
        }
    }
} 