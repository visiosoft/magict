package upworksolutions.themagictricks.helper

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.common.util.UnstableApi
import java.io.File

@UnstableApi
class VideoPlayerHelper(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private val cache: SimpleCache
    private val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
    private val dataSourceFactory: CacheDataSource.Factory

    init {
        // Initialize cache
        val cacheDir = File(context.cacheDir, "media")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
        cache = SimpleCache(cacheDir, cacheEvictor)

        // Initialize data source factory with caching
        val upstreamFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("MagicTricks/1.0")

        dataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun createPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .setBandwidthMeter(bandwidthMeter)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
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