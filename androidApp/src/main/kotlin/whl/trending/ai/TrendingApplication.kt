package whl.trending.ai

import android.app.Application
import com.aptabase.Aptabase

class TrendingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Aptabase with the provided App Key
        Aptabase.instance.initialize(this, "A-US-1808698868")
        
        // Track app launch
        Aptabase.instance.trackEvent("app_started")
    }
}
