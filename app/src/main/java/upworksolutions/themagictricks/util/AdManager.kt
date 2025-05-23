package upworksolutions.themagictricks.util

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError

class AdManager private constructor(private val context: Context) {
    private var mInterstitialAd: InterstitialAd? = null
    private var lastAdShowTime: Long = 0
    private var appLaunchTime: Long = 0
    private val COOLDOWN_PERIOD = 3 * 60 * 1000 // 3 minutes in milliseconds
    private val INITIAL_GRACE_PERIOD = 3 * 60 * 1000 // 3 minutes grace period after app launch
    private val prefs: SharedPreferences = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)
    private var isAdLoading = false
    private var isAdShowing = false
    
    init {
        // Initialize last ad show time from preferences
        lastAdShowTime = prefs.getLong("last_ad_show_time", 0)
        appLaunchTime = System.currentTimeMillis()
    }

    fun loadInterstitialAd() {
        if (isAdLoading || mInterstitialAd != null) return
        
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdMobConfig.getInterstitialAdUnitId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    isAdLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    isAdLoading = false
                }
            }
        )
    }

    fun showInterstitialAd(onAdClosed: () -> Unit) {
        if (isAdShowing) {
            onAdClosed()
            return
        }

        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShowTime
        val timeSinceAppLaunch = currentTime - appLaunchTime

        // Don't show ads during initial grace period
        if (timeSinceAppLaunch < INITIAL_GRACE_PERIOD) {
            onAdClosed()
            return
        }

        if (timeSinceLastAd < COOLDOWN_PERIOD) {
            // Show cooldown message
            val remainingMinutes = (COOLDOWN_PERIOD - timeSinceLastAd) / 60000
            Toast.makeText(
                context,
                "Please wait ${remainingMinutes + 1} minutes before showing another ad",
                Toast.LENGTH_SHORT
            ).show()
            onAdClosed()
            return
        }

        if (mInterstitialAd != null) {
            isAdShowing = true
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    lastAdShowTime = System.currentTimeMillis()
                    prefs.edit().putLong("last_ad_show_time", lastAdShowTime).apply()
                    isAdShowing = false
                    loadInterstitialAd() // Load the next interstitial
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    mInterstitialAd = null
                    isAdShowing = false
                    loadInterstitialAd() // Load the next interstitial
                    onAdClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    // Ad is showing
                }
            }
            mInterstitialAd?.show(context as android.app.Activity)
        } else {
            // If no ad is available, load one for next time
            loadInterstitialAd()
            onAdClosed()
        }
    }

    fun canShowAd(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShowTime
        val timeSinceAppLaunch = currentTime - appLaunchTime
        return timeSinceLastAd >= COOLDOWN_PERIOD && timeSinceAppLaunch >= INITIAL_GRACE_PERIOD
    }

    fun getTimeUntilNextAd(): Long {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShowTime
        val timeSinceAppLaunch = currentTime - appLaunchTime
        
        return when {
            timeSinceAppLaunch < INITIAL_GRACE_PERIOD -> INITIAL_GRACE_PERIOD - timeSinceAppLaunch
            timeSinceLastAd < COOLDOWN_PERIOD -> COOLDOWN_PERIOD - timeSinceLastAd
            else -> 0
        }
    }

    companion object {
        @Volatile
        private var instance: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager(context.applicationContext).also { instance = it }
            }
        }
    }
} 