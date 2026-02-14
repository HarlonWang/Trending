package whl.trending.ai.core.platform

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun openAppSettings() {
    // Optional for Android
}

actual fun isIosPlatform(): Boolean = false
