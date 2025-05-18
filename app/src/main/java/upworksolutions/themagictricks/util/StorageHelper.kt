package upworksolutions.themagictricks.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class StorageHelper @Inject constructor(
    private val storage: FirebaseStorage,
    private val context: Context
) {
    private val videosRef = storage.reference.child("videos")
    private val thumbnailsRef = storage.reference.child("thumbnails")
    
    sealed class UploadResult {
        data class Progress(val progress: Int) : UploadResult()
        data class Success(val videoUrl: String, val thumbnailUrl: String) : UploadResult()
        data class Error(val exception: Exception) : UploadResult()
    }
    
    fun uploadVideo(
        videoUri: Uri,
        trickId: String,
        maxRetries: Int = 3
    ): Flow<UploadResult> = kotlinx.coroutines.flow.flow {
        var currentRetry = 0
        var lastException: Exception? = null

        while (currentRetry < maxRetries) {
            try {
                // Generate thumbnail first
                val thumbnailBitmap = generateThumbnail(videoUri)
                val thumbnailUrl = uploadThumbnail(thumbnailBitmap, trickId)

                // Upload video with progress tracking
                val videoRef = videosRef.child("$trickId.mp4")
                val uploadTask = videoRef.putFile(videoUri)

                // Track upload progress
                val progressChannel = kotlinx.coroutines.channels.Channel<Int>()
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                    progressChannel.trySend(progress)
                }

                // Launch a coroutine to emit progress updates
                val progressJob = kotlinx.coroutines.GlobalScope.launch {
                    for (progress in progressChannel) {
                        emit(UploadResult.Progress(progress))
                    }
                }

                // Wait for upload to complete
                val videoUrl = uploadTask.await().storage.downloadUrl.await().toString()
                progressChannel.close()
                progressJob.join()

                // Emit success with both URLs
                emit(UploadResult.Success(videoUrl, thumbnailUrl))
                return@flow
            } catch (e: Exception) {
                lastException = e
                currentRetry++
                if (currentRetry < maxRetries) {
                    kotlinx.coroutines.delay(1000L * (1 shl currentRetry))
                }
            }
        }
        // If we get here, all retries failed
        emit(UploadResult.Error(lastException ?: Exception("Upload failed after $maxRetries attempts")))
    }.flowOn(Dispatchers.IO)
    
    private suspend fun generateThumbnail(videoUri: Uri): Bitmap = withContext(Dispatchers.IO) {
        // TODO: Implement actual thumbnail extraction
        // For now, return a dummy Bitmap to avoid coroutine and getCurrentFrame issues
        Bitmap.createBitmap(320, 180, Bitmap.Config.ARGB_8888)
    }
    
    private suspend fun uploadThumbnail(bitmap: Bitmap, trickId: String): String {
        val thumbnailRef = thumbnailsRef.child("$trickId.jpg")
        
        // Compress bitmap to JPEG
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()
        
        // Upload thumbnail
        return thumbnailRef.putBytes(data)
            .await()
            .storage
            .downloadUrl
            .await()
            .toString()
    }
    
    suspend fun deleteVideo(trickId: String) {
        try {
            // Delete video
            videosRef.child("$trickId.mp4").delete().await()
            // Delete thumbnail
            thumbnailsRef.child("$trickId.jpg").delete().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun getDownloadUrl(path: String): String? {
        return try {
            storage.reference.child(path).downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
} 