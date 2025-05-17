package com.example.magictricks.repository

import com.example.magictricks.model.Trick
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TricksRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val tricksCollection = firestore.collection("tricks")
    
    fun getFeaturedTricks(): Flow<List<Trick>> = flow {
        try {
            val snapshot = tricksCollection
                .whereEqualTo("isFeatured", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            
            val tricks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Trick::class.java)?.copy(id = doc.id)
            }
            emit(tricks)
        } catch (e: Exception) {
            // Handle error
            emit(emptyList())
        }
    }
    
    fun getTricksByCategory(category: String): Flow<List<Trick>> = flow {
        try {
            val snapshot = tricksCollection
                .whereArrayContains("categories", category)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val tricks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Trick::class.java)?.copy(id = doc.id)
            }
            emit(tricks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    fun getUserFavorites(userId: String): Flow<List<Trick>> = flow {
        try {
            val favoritesRef = firestore
                .collection("users")
                .document(userId)
                .collection("favorites")
            
            val snapshot = favoritesRef.get().await()
            val trickIds = snapshot.documents.map { it.id }
            
            if (trickIds.isEmpty()) {
                emit(emptyList())
                return@flow
            }
            
            val tricksSnapshot = tricksCollection
                .whereIn("id", trickIds)
                .get()
                .await()
            
            val tricks = tricksSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Trick::class.java)?.copy(id = doc.id)
            }
            emit(tricks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    suspend fun toggleFavorite(userId: String, trickId: String): Boolean {
        return try {
            val favoritesRef = firestore
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(trickId)
            
            val doc = favoritesRef.get().await()
            if (doc.exists()) {
                favoritesRef.delete().await()
                false
            } else {
                favoritesRef.set(mapOf("addedAt" to System.currentTimeMillis())).await()
                true
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addTrick(trick: Trick): String? {
        return try {
            val docRef = tricksCollection.add(trick.toMap()).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateTrick(trick: Trick): Boolean {
        return try {
            tricksCollection.document(trick.id)
                .set(trick.toMap())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteTrick(trickId: String): Boolean {
        return try {
            tricksCollection.document(trickId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
} 