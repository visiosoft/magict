package upworksolutions.themagictricks

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import upworksolutions.themagictricks.util.AppOpenAdManager

@HiltAndroidApp
class MagicTricksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        MobileAds.initialize(this)
        
        // Initialize AppOpenAdManager
        AppOpenAdManager.getInstance(this).loadAd()
    }
} 