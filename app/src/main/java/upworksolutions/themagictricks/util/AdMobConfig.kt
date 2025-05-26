package upworksolutions.themagictricks.util

object AdMobConfig {
    // App ID
    const val APP_ID = "ca-app-pub-9773068853653447~5630707376"

    // Test Ad Unit IDs
    object Test {
        const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"
        const val NATIVE_ADVANCED_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    }

    // Production Ad Unit IDs
    object Production {
        const val BANNER_AD_UNIT_ID = "ca-app-pub-9773068853653447/1453749099"
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9773068853653447/2798620351"
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-9773068853653447/1453749099"
        const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-9773068853653447/2079518750"
        const val NATIVE_ADVANCED_AD_UNIT_ID = "ca-app-pub-9773068853653447/3604337508"
    }

    // Current environment
    private const val IS_TESTING = false

    // Get current ad unit IDs based on environment
    fun getBannerAdUnitId(): String {
        return if (IS_TESTING) Test.BANNER_AD_UNIT_ID else Production.BANNER_AD_UNIT_ID
    }

    fun getInterstitialAdUnitId(): String {
        return if (IS_TESTING) Test.INTERSTITIAL_AD_UNIT_ID else Production.INTERSTITIAL_AD_UNIT_ID
    }

    fun getRewardedAdUnitId(): String {
        return if (IS_TESTING) Test.REWARDED_AD_UNIT_ID else Production.REWARDED_AD_UNIT_ID
    }

    fun getAppOpenAdUnitId(): String {
        return if (IS_TESTING) Test.APP_OPEN_AD_UNIT_ID else Production.APP_OPEN_AD_UNIT_ID
    }

    fun getNativeAdvancedAdUnitId(): String {
        return if (IS_TESTING) Test.NATIVE_ADVANCED_AD_UNIT_ID else Production.NATIVE_ADVANCED_AD_UNIT_ID
    }
} 