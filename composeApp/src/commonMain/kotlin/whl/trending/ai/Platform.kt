package whl.trending.ai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform