package upworksolutions.themagictricks.repository

import upworksolutions.themagictricks.model.Trick
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class TrickRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val tricksCollection = firestore.collection("tricks")

    suspend fun getTricks(): Flow<List<Trick>> = flow {
        try {
            val snapshot = tricksCollection
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val tricks = snapshot.documents.mapNotNull { doc ->
                Trick.fromFirestore(doc.id, doc.data ?: return@mapNotNull null)
            }
            emit(tricks)
        } catch (e: Exception) {
            // In a real app, you'd want to handle this error properly
            emit(emptyList())
        }
    }

    suspend fun getTricksByCategory(category: String): Flow<List<Trick>> = flow {
        try {
            val snapshot = tricksCollection
                .whereArrayContains("categories", category)
                .get()
                .await()
            
            val tricks = snapshot.documents.mapNotNull { doc ->
                Trick.fromFirestore(doc.id, doc.data ?: return@mapNotNull null)
            }
            emit(tricks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getProTricks(): Flow<List<Trick>> = flow {
        try {
            val snapshot = tricksCollection
                .whereEqualTo("isPro", true)
                .get()
                .await()
            
            val tricks = snapshot.documents.mapNotNull { doc ->
                Trick.fromFirestore(doc.id, doc.data ?: return@mapNotNull null)
            }
            emit(tricks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addTrick(trick: Trick): Result<String> = try {
        val docRef = tricksCollection.add(trick.toMap()).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateTrick(trick: Trick): Result<Unit> = try {
        tricksCollection.document(trick.id)
            .set(trick.toMap())
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteTrick(trickId: String): Result<Unit> = try {
        tricksCollection.document(trickId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
} 