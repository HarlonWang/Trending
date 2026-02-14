package whl.trending.ai.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun openAppSettings()

expect fun openUrl(url: String, targetPackage: String? = null)

expect fun getAppVersion(): String

expect fun isIosPlatform(): Boolean
