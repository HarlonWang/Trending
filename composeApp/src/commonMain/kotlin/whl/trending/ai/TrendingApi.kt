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
    private val baseUrl = "https://cdn.jsdelivr.net/gh/HarlonWang/github-ai-trending-api@main/api/trending"

    suspend fun fetchTrending(type: String): List<TrendingRepo> {
        val endpoint = when(type) {
            "今日" -> "daily"
            "每周" -> "weekly"
            "每月" -> "monthly"
            else -> "daily"
        }
        val url = "$baseUrl/$endpoint/all.json"
        println("TrendingApi: Fetching from $url")
        return try {
            val response = client.get(url)
            println("TrendingApi: Response status: ${response.status}")
            val body = response.body<List<TrendingRepo>>()
            println("TrendingApi: Successfully fetched ${body.size} repos for $type")
            body
        } catch (e: Exception) {
            println("TrendingApi: Error fetching $type: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
