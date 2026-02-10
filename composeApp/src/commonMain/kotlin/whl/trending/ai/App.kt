package whl.trending.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import trending.composeapp.generated.resources.GitHub_Invertocat_Black
import trending.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val tabs = listOf("今日", "每周", "每月")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Trending AI") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(Res.drawable.GitHub_Invertocat_Black),
                                contentDescription = "GitHub",
                                modifier = Modifier.size(24.dp)
                            )
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
                ) { _ ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = 20,
                            key = { index -> index }
                        ) { index ->
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
                                        Text(text = "${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.W500)
                                    }
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "anthropic/claude-3-opus",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W500,
                                        color = Color(0xFF1C1B1F)
                                    )
                                    Text(
                                        text = "Anthropic\\'s most powerful AI model. Excels at highly complex tasks with strong reasoning capabilities.",
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = Color(0xFF49454F)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = Color(0xFFE8DEF8),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = "这是 Anthropic 推出的旗舰级 AI 模型，在复杂推理、代码生成和创意写作方面表现出色。特别适合需要深度思考的应用场景。",
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = Color(0xFF21005D)
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(12.dp),
                                            shape = CircleShape,
                                            color = Color(0xFF3572A5)
                                        ) {}
                                        Text(text = "Python", fontSize = 14.sp, color = Color(0xFF49454F))
                                        Text(text = "2847 stars today", fontSize = 14.sp, color = Color(0xFF49454F))
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

        }
    }
}
