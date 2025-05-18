package upworksolutions.themagictricks.util

import upworksolutions.themagictricks.model.Trick
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataGenerator @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val tricksCollection = firestore.collection("tricks")
    
    suspend fun generateSampleData() {
        val sampleTricks = listOf(
            Trick(
                id = "",
                title = "Amazing Card Vanish",
                videoUrl = "https://example.com/videos/card-vanish.mp4",
                thumbnailUrl = "https://example.com/thumbnails/card-vanish.jpg",
                duration = 180,
                categories = listOf("Card Tricks", "Beginner"),
                isPro = false,
                isFeatured = true
            ),
            Trick(
                id = "",
                title = "Mind-Blowing Coin Magic",
                videoUrl = "https://example.com/videos/coin-magic.mp4",
                thumbnailUrl = "https://example.com/thumbnails/coin-magic.jpg",
                duration = 240,
                categories = listOf("Coin Tricks", "Intermediate"),
                isPro = true,
                isFeatured = true
            ),
            Trick(
                id = "",
                title = "Rope Through Neck",
                videoUrl = "https://example.com/videos/rope-trick.mp4",
                thumbnailUrl = "https://example.com/thumbnails/rope-trick.jpg",
                duration = 300,
                categories = listOf("Rope Tricks", "Advanced"),
                isPro = true,
                isFeatured = false
            )
        )
        
        // Add tricks to Firestore
        sampleTricks.forEach { trick ->
            try {
                tricksCollection.add(trick.toMap()).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    suspend fun generateSampleUserData(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        val favoritesRef = userRef.collection("favorites")
        
        // Create user document
        userRef.set(mapOf(
            "role" to "user",
            "createdAt" to System.currentTimeMillis()
        )).await()
        
        // Add some favorites
        val tricks = tricksCollection.limit(2).get().await()
        tricks.documents.forEach { doc ->
            favoritesRef.document(doc.id).set(mapOf(
                "addedAt" to System.currentTimeMillis()
            )).await()
        }
    }
} 