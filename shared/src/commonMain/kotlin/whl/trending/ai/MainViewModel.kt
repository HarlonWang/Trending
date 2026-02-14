package whl.trending.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val repos: List<TrendingRepo> = emptyList(),
    val since: String = "",
    val capturedAt: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedPeriod: String = "daily",
    val selectedLanguage: String = "all",
    val error: String? = null
)

class MainViewModel(private val api: TrendingApi = TrendingApi()) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        fetchData()
    }

    fun fetchData(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
                delay(500)
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            try {
                val response = api.fetchTrending(_uiState.value.selectedPeriod, _uiState.value.selectedLanguage)
                _uiState.update { 
                    it.copy(
                        repos = response.data,
                        since = response.since,
                        capturedAt = response.capturedAt,
                        isLoading = false,
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isRefreshing = false, 
                        error = e.message ?: "Unknown Error"
                    ) 
                }
            }
        }
    }

    fun updateFilter(period: String, language: String) {
        if (_uiState.value.selectedPeriod == period && _uiState.value.selectedLanguage == language) return
        
        _uiState.update { 
            it.copy(
                selectedPeriod = period,
                selectedLanguage = language
            )
        }
        fetchData()
    }
}
