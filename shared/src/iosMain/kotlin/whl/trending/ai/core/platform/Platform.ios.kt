package whl.trending.ai.core.platform

import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.Foundation.NSURL

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun openAppSettings() {
    val url = NSURL(string = UIApplicationOpenSettingsURLString)
    val app = UIApplication.sharedApplication
    if (app.canOpenURL(url)) {
        app.openURL(
            url,
            options = emptyMap<Any?, Any?>(),
            completionHandler = { _ -> }
        )
    }
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(
            nsUrl,
            options = emptyMap<Any?, Any?>(),
            completionHandler = { _ -> }
        )
    }
}

actual fun isIosPlatform(): Boolean = true
