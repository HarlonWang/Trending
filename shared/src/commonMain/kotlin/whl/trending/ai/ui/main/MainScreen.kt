package whl.trending.ai.ui.main

import whl.trending.ai.data.model.TrendingRepo
import whl.trending.ai.data.model.TrendingAiSummary
import whl.trending.ai.core.platform.openUrl
import whl.trending.ai.core.Constants
import whl.trending.ai.data.local.globalSettingsManager
import whl.trending.ai.data.local.AppLanguage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import trending.shared.generated.resources.GitHub_Invertocat_Black
import trending.shared.generated.resources.GitHub_Invertocat_White
import trending.shared.generated.resources.Res
import trending.shared.generated.resources.app_name
import trending.shared.generated.resources.deepseek_color
import trending.shared.generated.resources.error_fetch
import trending.shared.generated.resources.filter_done
import trending.shared.generated.resources.filter_language
import trending.shared.generated.resources.filter_options
import trending.shared.generated.resources.filter_period
import trending.shared.generated.resources.filter_reset
import trending.shared.generated.resources.gemini_color
import trending.shared.generated.resources.icon_flame
import trending.shared.generated.resources.last_updated
import trending.shared.generated.resources.no_data
import trending.shared.generated.resources.retry
import trending.shared.generated.resources.settings
import trending.shared.generated.resources.stars_since
import trending.shared.generated.resources.tab_daily
import trending.shared.generated.resources.tab_monthly
import trending.shared.generated.resources.tab_weekly

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TrendingTopBar(
                selectedPeriod = uiState.selectedPeriod,
                selectedLanguage = uiState.selectedLanguage,
                scrollBehavior = scrollBehavior,
                onTitleClick = { showFilterSheet = true },
                onNavigateToSettings = onNavigateToSettings
            )
        },
    ) { innerPadding ->
        RepoList(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onRefresh = { viewModel.fetchData(isRefresh = true) }
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedPeriod = uiState.selectedPeriod,
            selectedLanguage = uiState.selectedLanguage,
            onDismiss = { showFilterSheet = false },
            onConfirm = { period, language ->
                viewModel.updateFilter(period, language)
                showFilterSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendingTopBar(
    selectedPeriod: String,
    selectedLanguage: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onTitleClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val periodLabel = when (selectedPeriod) {
        "daily" -> stringResource(Res.string.tab_daily)
        "weekly" -> stringResource(Res.string.tab_weekly)
        "monthly" -> stringResource(Res.string.tab_monthly)
        else -> selectedPeriod
    }

    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .clickable { onTitleClick() }
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }
                Text(
                    text = "$periodLabel · ${selectedLanguage.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(
                        if (isDarkTheme) Res.drawable.GitHub_Invertocat_White
                        else Res.drawable.GitHub_Invertocat_Black
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RepoList(
    uiState: MainUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    val state = rememberPullToRefreshState()
    
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        state = state,
        onRefresh = onRefresh,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = state,
                isRefreshing = uiState.isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator(modifier = Modifier.size(48.dp))
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(Res.string.error_fetch, uiState.error),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }

            uiState.repos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(Res.string.no_data))
                }
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = uiState.repos.size,
                    key = { index -> uiState.repos[index].url }
                ) { index ->
                    RepoItem(index = index, repo = uiState.repos[index], since = uiState.since)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }

                if (uiState.capturedAt.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.last_updated, uiState.capturedAt),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
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

@Composable
private fun RepoItem(index: Int, repo: TrendingRepo, since: String) {
    Row(
        modifier = Modifier
            .clickable { openUrl(repo.url, Constants.GITHUB_APP_PACKAGE) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.W500)
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

            repo.aiSummary?.let { summary ->
                AiSummaryBox(summary)
            }

            RepoMetadata(repo = repo, since = since)
        }
    }
}

@Composable
private fun AiSummaryBox(aiSummary: TrendingAiSummary) {
    val appLanguage by globalSettingsManager.appLanguage.collectAsState(AppLanguage.FOLLOW_SYSTEM)
    
    val contentToShow = when (appLanguage) {
        AppLanguage.CHINESE -> aiSummary.zh ?: aiSummary.en
        AppLanguage.ENGLISH -> aiSummary.en ?: aiSummary.zh
        AppLanguage.FOLLOW_SYSTEM -> {
            // Simplified system detection: if it's zh or en
            aiSummary.zh ?: aiSummary.en
        }
    } ?: return

    if (contentToShow.isEmpty()) return

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
        val aiIcon = when (aiSummary.source.lowercase()) {
            "gemini" -> Res.drawable.gemini_color
            "deepseek" -> Res.drawable.deepseek_color
            else -> Res.drawable.gemini_color
        }
        Icon(
            painter = painterResource(aiIcon),
            contentDescription = "AI-ICON",
            tint = Color.Unspecified,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Text(
            text = contentToShow,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun RepoMetadata(repo: TrendingRepo, since: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = repo.languageColor?.toColorOrNull() ?: MaterialTheme.colorScheme.outline
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
            text = stringResource(Res.string.stars_since, repo.currentPeriodStars, since),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FilterBottomSheet(
    selectedPeriod: String,
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val periods = listOf("daily", "weekly", "monthly")
    val languages = listOf("all", "javascript", "java", "go", "rust", "typescript", "c++", "c", "swift", "kotlin")
    
    var tempPeriod by remember { mutableStateOf(selectedPeriod) }
    var tempLanguage by remember { mutableStateOf(selectedLanguage) }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(Res.string.filter_options),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(Res.string.filter_period),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                periods.forEachIndexed { index, period ->
                    val label = when (period) {
                        "daily" -> stringResource(Res.string.tab_daily)
                        "weekly" -> stringResource(Res.string.tab_weekly)
                        "monthly" -> stringResource(Res.string.tab_monthly)
                        else -> period
                    }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size),
                        onClick = { tempPeriod = period },
                        selected = tempPeriod == period
                    ) { Text(label) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.filter_language),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languages.forEach { language ->
                    FilterChip(
                        selected = tempLanguage == language,
                        onClick = { tempLanguage = language },
                        label = { Text(language.replaceFirstChar { it.uppercase() }) },
                        leadingIcon = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        onConfirm("daily", "all")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.filter_reset))
                }

                Button(
                    onClick = { onConfirm(tempPeriod, tempLanguage) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.filter_done))
                }
            }
        }
    }
}

private fun String.toColorOrNull(): Color? {
    val hex = this.removePrefix("#")
    return if (hex.length == 6) {
        runCatching {
            Color((hex.toLong(16) or 0xFF000000L).toInt())
        }.getOrNull()
    } else {
        null
    }
}
