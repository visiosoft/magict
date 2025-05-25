package upworksolutions.themagictricks.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenAdManager private constructor(private val context: Context) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0
    private val TAG = "AppOpenAdManager"
    private val prefs: SharedPreferences = context.getSharedPreferences("app_open_ad_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val AD_EXPIRATION_TIME = 4 * 60 * 60 * 1000 // 4 hours in milliseconds
        private const val MINIMUM_TIME_BETWEEN_ADS = 30 * 60 * 1000 // 30 minutes in milliseconds
        private const val MAX_ADS_PER_SESSION = 3 // Maximum number of ads per app session
        private const val PREF_LAST_AD_SHOW_TIME = "last_ad_show_time"
        private const val PREF_ADS_SHOWN_THIS_SESSION = "ads_shown_this_session"
        private const val PREF_SESSION_START_TIME = "session_start_time"

        @Volatile
        private var instance: AppOpenAdManager? = null

        fun getInstance(context: Context): AppOpenAdManager {
            return instance ?: synchronized(this) {
                instance ?: AppOpenAdManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        // Initialize session if needed
        if (prefs.getLong(PREF_SESSION_START_TIME, 0) == 0L) {
            prefs.edit().apply {
                putLong(PREF_SESSION_START_TIME, System.currentTimeMillis())
                putInt(PREF_ADS_SHOWN_THIS_SESSION, 0)
                apply()
            }
        }
    }

    fun loadAd() {
        // Don't load ad if there's an existing ad or already loading
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        // Check if we've reached the maximum ads for this session
        if (prefs.getInt(PREF_ADS_SHOWN_THIS_SESSION, 0) >= MAX_ADS_PER_SESSION) {
            Log.d(TAG, "Maximum ads per session reached")
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AdMobConfig.getAppOpenAdUnitId(),
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "App open ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d(TAG, "App open ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing")
            return
        }

        // Check if we've reached the maximum ads for this session
        if (prefs.getInt(PREF_ADS_SHOWN_THIS_SESSION, 0) >= MAX_ADS_PER_SESSION) {
            Log.d(TAG, "Maximum ads per session reached")
            return
        }

        // Check minimum time between ads
        val lastAdShowTime = prefs.getLong(PREF_LAST_AD_SHOW_TIME, 0)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdShowTime < MINIMUM_TIME_BETWEEN_ADS) {
            Log.d(TAG, "Minimum time between ads not elapsed")
            return
        }

        // If the ad is not available, do not show the ad.
        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet")
            loadAd()
            return
        }

        // Check if enough time has passed since last ad
        if (currentTime - loadTime > AD_EXPIRATION_TIME) {
            Log.d(TAG, "The app open ad has expired")
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                // Update ad count and last show time
                prefs.edit().apply {
                    putLong(PREF_LAST_AD_SHOW_TIME, System.currentTimeMillis())
                    putInt(PREF_ADS_SHOWN_THIS_SESSION, prefs.getInt(PREF_ADS_SHOWN_THIS_SESSION, 0) + 1)
                    apply()
                }
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }

        isShowingAd = true
        appOpenAd?.show(activity)
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    fun resetSession() {
        prefs.edit().apply {
            putLong(PREF_SESSION_START_TIME, System.currentTimeMillis())
            putInt(PREF_ADS_SHOWN_THIS_SESSION, 0)
            apply()
        }
    }
} 