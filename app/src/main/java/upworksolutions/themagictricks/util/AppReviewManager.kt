package upworksolutions.themagictricks.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await

class AppReviewManager(private val context: Context) {
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    suspend fun requestReviewFlow(): ReviewInfo {
        return reviewManager.requestReviewFlow().await()
    }

    fun launchReviewFlow(activity: Activity, reviewInfo: ReviewInfo) {
        reviewManager.launchReviewFlow(activity, reviewInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Review flow launched successfully
                    Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                } else {
                    // Review flow failed to launch
                    Toast.makeText(context, "Could not launch review. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }
} 