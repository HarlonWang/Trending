package whl.trending.ai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun openAppSettings()

expect fun isIosPlatform(): Boolean
