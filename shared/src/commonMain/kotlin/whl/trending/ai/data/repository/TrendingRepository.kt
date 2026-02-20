package whl.trending.ai.data.repository

import whl.trending.ai.data.model.TrendingResponse
import whl.trending.ai.data.remote.TrendingApi

class TrendingRepository(private val api: TrendingApi = TrendingApi()) {
    suspend fun getTrending(
        period: String, 
        language: String, 
        providers: String? = null,
        summaryLang: String
    ): TrendingResponse {
        return api.fetchTrending(period, language, providers, summaryLang)
    }
}
