package upworksolutions.themagictricks.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import upworksolutions.themagictricks.model.Trick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object OfflineTrickDataProvider {
    private const val TAG = "OfflineTrickDataProvider"
    private var tricks: List<Trick>? = null
    private var lastLoadTime: Long = 0
    private const val CACHE_DURATION = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

    suspend fun getOfflineTricks(context: Context): List<Trick> = withContext(Dispatchers.IO) {
        if (shouldReloadData()) {
            loadOfflineTricksFromJson(context)
        }
        tricks ?: emptyList()
    }

    private fun shouldReloadData(): Boolean {
        return tricks == null || System.currentTimeMillis() - lastLoadTime > CACHE_DURATION
    }

    private suspend fun loadOfflineTricksFromJson(context: Context) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting to load offlinetricks.json from assets")
            
            // Try loading from cache first
            val cacheFile = File(context.cacheDir, "offlinetricks_cache.json")
            if (cacheFile.exists() && System.currentTimeMillis() - cacheFile.lastModified() < CACHE_DURATION) {
                try {
                    val cachedJson = cacheFile.readText()
                    if (parseJsonString(cachedJson)) {
                        Log.d(TAG, "Successfully loaded from cache")
                        return@withContext
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading from cache", e)
                }
            }

            // Try loading from assets
            try {
                val assetManager = context.assets
                val files = assetManager.list("")
                Log.d(TAG, "Available assets: ${files?.joinToString()}")
                
                if (files?.contains("offlinetricks.json") == true) {
                    val jsonString = context.assets.open("offlinetricks.json").bufferedReader().use { it.readText() }
                    Log.d(TAG, "Successfully read offlinetricks.json from assets, length: ${jsonString.length}")
                    if (parseJsonString(jsonString)) {
                        // Save to cache
                        cacheFile.writeText(jsonString)
                        return@withContext
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading from assets", e)
            }

            // If all attempts fail, set empty list
            tricks = emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadOfflineTricksFromJson", e)
            tricks = emptyList()
        }
    }

    private fun parseJsonString(jsonString: String): Boolean {
        return try {
            val type = object : TypeToken<OfflineTricksResponse>() {}.type
            val response = Gson().fromJson<OfflineTricksResponse>(jsonString, type)
            
            if (response.magic_tricks.isEmpty()) {
                Log.e(TAG, "No offline tricks found in JSON")
                false
            } else {
                Log.d(TAG, "Successfully loaded ${response.magic_tricks.size} offline tricks")
                tricks = response.magic_tricks.map { offlineTrick ->
                    Trick(
                        id = offlineTrick.id.toString(),
                        title = offlineTrick.title,
                        thumbnailUrl = offlineTrick.thumbnail,
                        subtitle = offlineTrick.subtitle,
                        description = offlineTrick.description,
                        itemsNeeded = offlineTrick.items_needed,
                        steps = offlineTrick.steps,
                        howItWorks = offlineTrick.how_it_works,
                        difficulty = offlineTrick.difficulty,
                        videoUrl = "" // Offline tricks don't have video URLs
                    )
                }
                lastLoadTime = System.currentTimeMillis()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON", e)
            false
        }
    }
} 