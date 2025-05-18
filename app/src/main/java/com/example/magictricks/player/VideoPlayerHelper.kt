package upworksolutions.themagictricks.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.common.PlaybackException

class VideoPlayerHelper private constructor(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private val TAG = "VideoPlayerHelper"

    companion object {
        @Volatile
        private var instance: VideoPlayerHelper? = null

        fun getInstance(context: Context): VideoPlayerHelper {
            return instance ?: synchronized(this) {
                instance ?: VideoPlayerHelper(context).also { instance = it }
            }
        }
    }

    fun createPlayer() {
        Log.d(TAG, "Creating new ExoPlayer instance")
        try {
            // Create data source factory
            val dataSourceFactory = DefaultDataSourceFactory(context)
            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

            // Create and configure ExoPlayer
            exoPlayer = ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                    playWhenReady = true
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            Log.d(TAG, "Playback state changed: $state")
                            when (state) {
                                Player.STATE_READY -> {
                                    Log.d(TAG, "Player is ready")
                                    play()
                                }
                                Player.STATE_BUFFERING -> Log.d(TAG, "Player is buffering")
                                Player.STATE_ENDED -> Log.d(TAG, "Player ended")
                                Player.STATE_IDLE -> Log.d(TAG, "Player is idle")
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(TAG, "Player error: ${error.message}", error)
                            Toast.makeText(context, "Error playing video: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            Log.d(TAG, "ExoPlayer created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating ExoPlayer", e)
            Toast.makeText(context, "Error initializing video player: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun preparePlayer(uri: Uri) {
        Log.d(TAG, "Preparing player with URI: $uri")
        try {
            if (exoPlayer == null) {
                Log.d(TAG, "Player is null, creating new instance")
                createPlayer()
            }

            exoPlayer?.let { player ->
                val mediaItem = MediaItem.fromUri(uri)
                player.setMediaItem(mediaItem)
                player.prepare()
                Log.d(TAG, "Player prepared successfully")
            } ?: run {
                Log.e(TAG, "Failed to create player")
                Toast.makeText(context, "Failed to initialize video player", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error preparing player", e)
            Toast.makeText(context, "Error preparing video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun play() {
        Log.d(TAG, "Attempting to play video")
        try {
            exoPlayer?.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing video", e)
            Toast.makeText(context, "Error playing video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun release() {
        Log.d(TAG, "Releasing player resources")
        try {
            exoPlayer?.release()
            exoPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing player", e)
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer
} 