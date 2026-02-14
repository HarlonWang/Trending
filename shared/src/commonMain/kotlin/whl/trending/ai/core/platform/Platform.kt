package whl.trending.ai.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun openAppSettings()

expect fun openUrl(url: String)

expect fun isIosPlatform(): Boolean
