package whl.trending.ai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay


data object Main
data object Settings

@Composable
@Preview
fun App() {
    val backStack = remember { mutableStateListOf<Any>(Main) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Main -> NavEntry(key) {
                    MainScreen(
                        onNavigateToSettings = {
                            backStack.add(Settings)
                        }
                    )
                }

                is Settings -> NavEntry(key) {
                    SettingsScreen(
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                else -> {
                    error("Unknown route: $key")
                }
            }
        }
    )
}
