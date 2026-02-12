package whl.trending.ai

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

actual fun isIosPlatform(): Boolean = true
