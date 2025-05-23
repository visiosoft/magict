package upworksolutions.themagictricks.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import upworksolutions.themagictricks.model.Trick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.io.File

object TrickDataProvider {
    private const val TAG = "TrickDataProvider"
    private const val FALLBACK_URL = "https://raw.githubusercontent.com/visiosoft/videostreaming/refs/heads/main/tricks.json"
    private var tricks: List<Trick>? = null
    private var lastLoadTime: Long = 0
    private const val CACHE_DURATION = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

    suspend fun getTrendingTricks(context: Context): List<Trick> = withContext(Dispatchers.IO) {
        if (shouldReloadData()) {
            loadTricksFromJson(context)
        }
        tricks ?: emptyList()
    }

    private fun shouldReloadData(): Boolean {
        return tricks == null || System.currentTimeMillis() - lastLoadTime > CACHE_DURATION
    }

    private suspend fun loadTricksFromJson(context: Context) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting to load tricks.json from assets")
            
            // Try loading from cache first
            val cacheFile = File(context.cacheDir, "tricks_cache.json")
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
                
                if (files?.contains("tricks.json") == true) {
                    val jsonString = context.assets.open("tricks.json").bufferedReader().use { it.readText() }
                    Log.d(TAG, "Successfully read tricks.json from assets, length: ${jsonString.length}")
                    if (parseJsonString(jsonString)) {
                        // Save to cache
                        cacheFile.writeText(jsonString)
                        return@withContext
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading from assets", e)
            }

            // Try loading from fallback URL
            try {
                Log.d(TAG, "Attempting to load from fallback URL: $FALLBACK_URL")
                val jsonString = URL(FALLBACK_URL).readText()
                Log.d(TAG, "Successfully read from fallback URL, length: ${jsonString.length}")
                if (parseJsonString(jsonString)) {
                    // Save to cache
                    cacheFile.writeText(jsonString)
                    return@withContext
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading from fallback URL", e)
            }

            // If all attempts fail, set empty list
            tricks = emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadTricksFromJson", e)
            tricks = emptyList()
        }
    }

    private fun parseJsonString(jsonString: String): Boolean {
        return try {
            val type = object : TypeToken<TricksResponse>() {}.type
            val response = Gson().fromJson<TricksResponse>(jsonString, type)
            
            if (response.tricks.isEmpty()) {
                Log.e(TAG, "No tricks found in JSON")
                false
            } else {
                Log.d(TAG, "Successfully loaded ${response.tricks.size} tricks")
                tricks = response.tricks
                lastLoadTime = System.currentTimeMillis()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON", e)
            false
        }
    }

    suspend fun getTricksByCategory(context: Context, category: String): List<Trick> = withContext(Dispatchers.IO) {
        if (shouldReloadData()) {
            loadTricksFromJson(context)
        }
        tricks?.filter { it.categories.contains(category) } ?: emptyList()
    }

    suspend fun getFeaturedTricks(context: Context): List<Trick> = withContext(Dispatchers.IO) {
        if (shouldReloadData()) {
            loadTricksFromJson(context)
        }
        tricks?.filter { it.isFeatured } ?: emptyList()
    }
} 