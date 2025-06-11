package upworksolutions.themagictricks

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import dagger.hilt.android.HiltAndroidApp
import upworksolutions.themagictricks.util.AppOpenAdManager

@HiltAndroidApp
class MagicTricksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        MobileAds.initialize(this)
        
        // Initialize AppLovin SDK
        AppLovinSdk.getInstance(this).initializeSdk { configuration: AppLovinSdkConfiguration ->
            // SDK is initialized, start loading ads
        }
        
        // Initialize AppOpenAdManager
        AppOpenAdManager.getInstance(this).loadAd()
    }
} 