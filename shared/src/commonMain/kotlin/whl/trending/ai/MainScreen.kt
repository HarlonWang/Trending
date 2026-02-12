package whl.trending.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import trending.shared.generated.resources.GitHub_Invertocat_Black
import trending.shared.generated.resources.GitHub_Invertocat_White
import trending.shared.generated.resources.Res
import trending.shared.generated.resources.deepseek_color
import trending.shared.generated.resources.gemini_color
import trending.shared.generated.resources.icon_flame

fun String.toColorOrNull(): Color? {
    val hex = this.removePrefix("#")
    return if (hex.length == 6) {
        runCatching {
            Color((hex.toLong(16) or 0xFF000000L).toInt())
        }.getOrNull()
    } else {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(onNavigateToSettings: () -> Unit) {
    val tabs = listOf("今日", "本周", "本月")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val api = remember { TrendingApi() }
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Store data for each tab
    var dailyData by remember { mutableStateOf(TrendingResponse()) }
    var weeklyData by remember { mutableStateOf(TrendingResponse()) }
    var monthlyData by remember { mutableStateOf(TrendingResponse()) }
    var dailyLoading by remember { mutableStateOf(true) }
    var weeklyLoading by remember { mutableStateOf(true) }
    var monthlyLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        dailyLoading = true
        dailyData = api.fetchTrending("今日")
        dailyLoading = false

        weeklyLoading = true
        weeklyData = api.fetchTrending("本周")
        weeklyLoading = false

        monthlyLoading = true
        monthlyData = api.fetchTrending("本月")
        monthlyLoading = false
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Trending AI") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(
                                if (isDarkTheme) {
                                    Res.drawable.GitHub_Invertocat_White
                                } else {
                                    Res.drawable.GitHub_Invertocat_Black
                                }
                            ),
                            contentDescription = "GitHub",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tabTitle) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val data = when (pageIndex) {
                    0 -> dailyData
                    1 -> weeklyData
                    else -> monthlyData
                }
                val repos = data.data
                val isLoading = when (pageIndex) {
                    0 -> dailyLoading
                    1 -> weeklyLoading
                    else -> monthlyLoading
                }

                var isRefreshing by remember { mutableStateOf(false) }
                val state = rememberPullToRefreshState()

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    state = state,
                    onRefresh = {
                        coroutineScope.launch {
                            isRefreshing = true
                            delay(500)
                            val refreshed = api.fetchTrending(tabs[pageIndex])
                            when (pageIndex) {
                                0 -> dailyData = refreshed
                                1 -> weeklyData = refreshed
                                else -> monthlyData = refreshed
                            }
                            isRefreshing = false
                        }
                    },
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            state = state,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator(modifier = Modifier.size(48.dp))
                            }
                        }

                        repos.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "暂无数据")
                            }
                        }

                        else -> LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = repos.size,
                                key = { index -> repos[index].url }
                            ) { index ->
                                val repo = repos[index]
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(28.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.W500
                                            )
                                        }
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "${repo.author}/${repo.repoName}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = repo.description,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        if (!repo.aiSummary?.content.isNullOrEmpty()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                val aiIcon =
                                                    when (repo.aiSummary.source.lowercase()) {
                                                        "gemini" -> Res.drawable.gemini_color
                                                        "deepseek" -> Res.drawable.deepseek_color
                                                        else -> Res.drawable.gemini_color
                                                    }
                                                Icon(
                                                    painter = painterResource(aiIcon),
                                                    contentDescription = "AI-ICON",
                                                    tint = Color.Unspecified,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .padding(top = 2.dp)
                                                )
                                                Text(
                                                    text = repo.aiSummary.content,
                                                    fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(12.dp),
                                                shape = CircleShape,
                                                color = repo.languageColor?.toColorOrNull()
                                                    ?: MaterialTheme.colorScheme.outline
                                            ) {}
                                            Text(
                                                text = repo.language ?: "",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Icon(
                                                painter = painterResource(Res.drawable.icon_flame),
                                                contentDescription = "Flame",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "${repo.currentPeriodStars} stars ${repo.since}",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            }

                            if (data.updateAt.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "最后更新于: ${data.updateAt}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
