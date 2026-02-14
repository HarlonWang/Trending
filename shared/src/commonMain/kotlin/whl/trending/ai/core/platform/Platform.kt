package whl.trending.ai.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun openAppSettings()

expect fun isIosPlatform(): Boolean
