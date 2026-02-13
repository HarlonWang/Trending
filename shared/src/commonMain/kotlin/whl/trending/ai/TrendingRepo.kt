package whl.trending.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingRepo(
    val rank: Int = 0,
    val author: String = "",
    val repoName: String = "",
    val url: String = "",
    val description: String = "",
    val language: String? = null,
    val languageColor: String? = null,
    val stars: Int = 0,
    val forks: Int = 0,
    val currentPeriodStars: Int = 0,
    val builtBy: List<TrendingContributor> = emptyList(),
    val aiSummary: TrendingAiSummary? = null
)

/**
 * Trending API 顶层响应结构。
 */
@Serializable
data class TrendingResponse(
    val count: Int = 0,
    val since: String = "",
    @SerialName("captured_at")
    val capturedAt: String = "",
    val data: List<TrendingRepo> = emptyList()
)

/**
 * 仓库贡献者基础信息。
 */
@Serializable
data class TrendingContributor(
    val username: String = "",
    val avatar: String = ""
)

/**
 * AI 摘要内容及来源。
 */
@Serializable
data class TrendingAiSummary(
    val content: String = "",
    val source: String = ""
)
