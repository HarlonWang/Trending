package whl.trending.ai

import kotlinx.serialization.Serializable

@Serializable
data class TrendingRepo(
    val author: String = "",
    val repoName: String = "",
    val description: String = "",
    val language: String? = null,
    val languageColor: String? = null,
    val currentPeriodStars: Int = 0,
    val aiSummary: String? = null,
    val since: String = "",
    val rank: Int = 0,
    val url: String = "",
    val stars: Int = 0,
    val forks: Int = 0
)