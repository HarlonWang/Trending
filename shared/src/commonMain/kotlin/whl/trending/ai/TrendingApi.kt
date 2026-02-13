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

    private val baseHost = "https://api.trendingai.cn"

    suspend fun fetchTrending(period: String): TrendingResponse {
        val endpoint = when (period.lowercase()) {
            "daily" -> "daily"
            "weekly" -> "weekly"
            "monthly" -> "monthly"
            else -> "daily"
        }

        val url = "$baseHost/trending/$endpoint/all.json"
        return try {
            val response = client.get(url)
            response.body<TrendingResponse>()
        } catch (e: Exception) {
            println("Fetch failed: ${e.message}")
            TrendingResponse()
        }
    }
}
