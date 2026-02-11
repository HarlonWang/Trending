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

    // Using jsDelivr mirror for better accessibility
    private val baseUrl = "https://cdn.jsdmirror.com/gh/HarlonWang/github-ai-trending-api@main/api/trending"
    // private val baseUrl = "https://cdn.jsdelivr.net/gh/HarlonWang/github-ai-trending-api@main/api/trending"
    // private val baseUrl = "https://raw.githubusercontent.com/HarlonWang/github-ai-trending-api/main/api/trending"

    suspend fun fetchTrending(type: String): List<TrendingRepo> {
        val endpoint = when(type) {
            "今日" -> "daily"
            "每周" -> "weekly"
            "每月" -> "monthly"
            else -> "daily"
        }
        val url = "$baseUrl/$endpoint/all.json"
        return try {
            val response = client.get(url)
            response.body<TrendingResponse>().data
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }
}
