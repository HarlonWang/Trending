package whl.trending.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class TrendingApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    // todo，暂时使用 jsdmirror 国内镜像源，后续可以根据所在国家区域切换源，
    //  以及在设置页面支持切换不同源，例如国外可以走 jsDelivr 或者 githubusercontent
    private val baseHost = "https://cdn.jsdmirror.com/gh/HarlonWang/github-ai-trending-api@main"
    private val apiPrefix = "api/trending"

    suspend fun fetchTrending(period: String): TrendingResponse {
        val endpoint = when (period.lowercase()) {
            "daily" -> "daily"
            "weekly" -> "weekly"
            "monthly" -> "monthly"
            else -> "daily"
        }

        val url = "$baseHost/$apiPrefix/$endpoint/all.json"
        return try {
            val response = client.get(url)
            response.body<TrendingResponse>()
        } catch (e: Exception) {
            println("Fetch failed: ${e.message}")
            TrendingResponse()
        }
    }
}
