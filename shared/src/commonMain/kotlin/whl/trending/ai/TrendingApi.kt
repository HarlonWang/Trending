package whl.trending.ai

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock

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
    private val archivePrefix = "archives"

    suspend fun fetchTrending(period: String): TrendingResponse {
        val endpoint = when (period.lowercase()) {
            "daily" -> "daily"
            "weekly" -> "weekly"
            "monthly" -> "monthly"
            else -> "daily"
        }

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        // 1. Try Archive URL first (e.g., .../archives/2026-02-11/daily/all.json)
        val archiveUrl = "$baseHost/$archivePrefix/$today/$endpoint/all.json"
        try {
            val response = client.get(archiveUrl)
            if (response.status.value == 200) {
                return response.body<TrendingResponse>()
            }
        } catch (e: Exception) {
            println("Archive fetch failed for $today, falling back to main api: ${e.message}")
        }

        // 2. Fallback to standard URL (e.g., .../api/trending/daily/all.json)
        val standardUrl = "$baseHost/$apiPrefix/$endpoint/all.json"
        return try {
            val response = client.get(standardUrl)
            response.body<TrendingResponse>()
        } catch (e: Exception) {
            println("Standard fetch failed: ${e.message}")
            TrendingResponse()
        }
    }
}
