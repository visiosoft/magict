package upworksolutions.themagictricks.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import upworksolutions.themagictricks.model.Trick

object TrickDataProvider {
    private var tricks: List<Trick>? = null

    fun getTrendingTricks(context: Context): List<Trick> {
        if (tricks == null) {
            loadTricksFromJson(context)
        }
        return tricks ?: emptyList()
    }

    private fun loadTricksFromJson(context: Context) {
        try {
            val jsonString = context.assets.open("tricks.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<TricksResponse>() {}.type
            val response = Gson().fromJson<TricksResponse>(jsonString, type)
            tricks = response.tricks
        } catch (e: Exception) {
            e.printStackTrace()
            tricks = emptyList()
        }
    }

    fun getTricksByCategory(context: Context, category: String): List<Trick> {
        if (tricks == null) {
            loadTricksFromJson(context)
        }
        return tricks?.filter { it.categories.contains(category) } ?: emptyList()
    }

    fun getFeaturedTricks(context: Context): List<Trick> {
        if (tricks == null) {
            loadTricksFromJson(context)
        }
        return tricks?.filter { it.isFeatured } ?: emptyList()
    }
} 