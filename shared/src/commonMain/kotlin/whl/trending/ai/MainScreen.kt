package whl.trending.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import org.jetbrains.compose.resources.stringResource
import trending.shared.generated.resources.GitHub_Invertocat_Black
import trending.shared.generated.resources.GitHub_Invertocat_White
import trending.shared.generated.resources.Res
import trending.shared.generated.resources.app_name
import trending.shared.generated.resources.deepseek_color
import trending.shared.generated.resources.gemini_color
import trending.shared.generated.resources.icon_flame
import trending.shared.generated.resources.last_updated
import trending.shared.generated.resources.no_data
import trending.shared.generated.resources.settings
import trending.shared.generated.resources.stars_since
import trending.shared.generated.resources.tab_daily
import trending.shared.generated.resources.tab_monthly
import trending.shared.generated.resources.tab_weekly

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
    val periods = listOf("daily", "weekly", "monthly")
    val languages = listOf(
        "all", "javascript", "java", "go", "rust",
        "typescript", "c++", "c", "swift", "kotlin"
    )

    var selectedPeriod by remember { mutableStateOf(periods[0]) }
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    val coroutineScope = rememberCoroutineScope()
    val api = remember { TrendingApi() }
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var trendingData by remember { mutableStateOf(TrendingResponse()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }

    val fetchData = suspend {
        val data = api.fetchTrending(selectedPeriod, selectedLanguage)
        trendingData = data
    }

    LaunchedEffect(selectedPeriod, selectedLanguage) {
        isLoading = true
        fetchData()
        isLoading = false
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
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
                        Icon(Icons.Default.Settings, contentDescription = stringResource(Res.string.settings))
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
            // Period Filter
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(periods) { period ->
                    val label = when (period) {
                        "daily" -> stringResource(Res.string.tab_daily)
                        "weekly" -> stringResource(Res.string.tab_weekly)
                        "monthly" -> stringResource(Res.string.tab_monthly)
                        else -> period
                    }
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(label) },
                        leadingIcon = if (selectedPeriod == period) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
            }

            // Language Filter
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(languages) { language ->
                    FilterChip(
                        selected = selectedLanguage == language,
                        onClick = { selectedLanguage = language },
                        label = { Text(language.replaceFirstChar { it.uppercase() }) },
                        leadingIcon = if (selectedLanguage == language) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
            }

            HorizontalDivider()

            val state = rememberPullToRefreshState()
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                state = state,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        delay(500)
                        fetchData()
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

                    trendingData.data.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(Res.string.no_data))
                        }
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = trendingData.data.size,
                            key = { index -> trendingData.data[index].url }
                        ) { index ->
                            val repo = trendingData.data[index]
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
                                            text = stringResource(
                                                Res.string.stars_since,
                                                repo.currentPeriodStars,
                                                trendingData.since
                                            ),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }

                        if (trendingData.capturedAt.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(Res.string.last_updated, trendingData.capturedAt),
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
