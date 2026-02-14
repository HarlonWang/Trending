package whl.trending.ai.data.remote

import whl.trending.ai.data.model.TrendingResponse

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

open class TrendingApi {
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

    open suspend fun fetchTrending(period: String, language: String): TrendingResponse {
        val endpoint = when (period.lowercase()) {
            "daily", "weekly", "monthly" -> period.lowercase()
            else -> "daily"
        }
        val lang = language.lowercase()

        val url = "$baseHost/trending/$endpoint/$lang.json"
        val response = client.get(url)
        return response.body<TrendingResponse>()
    }
}
