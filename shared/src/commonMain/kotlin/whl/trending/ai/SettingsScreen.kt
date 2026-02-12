package whl.trending.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 分组 1: 个性化
            item { SettingsHeader("个性化") }
            item {
                var isDarkMode by remember { mutableStateOf(false) }
                ListItem(
                    headlineContent = { Text("深色模式") },
                    supportingContent = { Text("开启后界面将切换为深色色调") },
                    leadingContent = { Icon(Icons.Default.Palette, null) },
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("主题色彩") },
                    supportingContent = { Text("自定义应用的主题颜色") },
                    leadingContent = { Icon(Icons.Default.Palette, null) },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // 分组 2: 应用设置
            item { SettingsHeader("应用设置") }
            item {
                ListItem(
                    headlineContent = { Text("语言设置") },
                    trailingContent = {
                        Text(
                            "简体中文",
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Language, null) },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("数据源设置") },
                    supportingContent = { Text("当前使用: Gemini API") },
                    leadingContent = { Icon(Icons.Default.Storage, null) },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            // 分组 3: 关于
            item { SettingsHeader("关于") }
            item {
                ListItem(
                    headlineContent = { Text("检查更新") },
                    trailingContent = { Text("v1.0.0", color = MaterialTheme.colorScheme.outline) },
                    leadingContent = { Icon(Icons.Default.Refresh, null) },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("关于我们") },
                    supportingContent = { Text("了解更多关于 Trending AI 的信息") },
                    leadingContent = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun SettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
