package whl.trending.ai.core.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import java.lang.ref.WeakReference

object AndroidContextHolder {
    private var contextRef: WeakReference<Context>? = null

    fun initialize(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun get(): Context? = contextRef?.get()
}

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun openAppSettings() {
    val context = AndroidContextHolder.get() ?: return
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

actual fun openUrl(url: String) {
    val context = AndroidContextHolder.get() ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    
    // Attempt to open with GitHub App first
    intent.setPackage("com.github.android")
    
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser if GitHub app is not installed
        intent.setPackage(null)
        context.startActivity(intent)
    }
}

actual fun isIosPlatform(): Boolean = false
