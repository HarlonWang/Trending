package whl.trending.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    class FakeTrendingApi(
        private val responseDelay: Long = 0,
        private var shouldFail: Boolean = false
    ) : TrendingApi() {
        var callCount = 0
        var lastPeriod: String? = null

        override suspend fun fetchTrending(period: String, language: String): TrendingResponse {
            callCount++
            lastPeriod = period
            if (responseDelay > 0) delay(responseDelay)
            if (shouldFail) throw Exception("Network Error")
            
            return TrendingResponse(
                data = listOf(TrendingRepo(repoName = "Repo for $period")),
                since = period
            )
        }
        
        fun setFail(fail: Boolean) {
            shouldFail = fail
        }
    }

    @Test
    fun testInitialFetchSuccess() = runTest {
        val fakeApi = FakeTrendingApi()
        val viewModel = MainViewModel(fakeApi)

        // Initially loading (since fetchData is called in init)
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.repos.size)
        assertEquals("Repo for daily", viewModel.uiState.value.repos[0].repoName)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun testFetchError() = runTest {
        val fakeApi = FakeTrendingApi(shouldFail = true)
        val viewModel = MainViewModel(fakeApi)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.repos.isEmpty())
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Network Error", viewModel.uiState.value.error)
    }

    @Test
    fun testConcurrencyRaceCondition() = runTest {
        val fakeApi = FakeTrendingApi(responseDelay = 1000)
        val viewModel = MainViewModel(fakeApi)

        // Initially, first call is made by init block
        // Move time forward a bit
        advanceTimeBy(100) 
        assertEquals(1, fakeApi.callCount)

        // Trigger a new filter update which should cancel the first one
        viewModel.updateFilter("weekly", "all")
        
        // Give the second call a chance to start and reach its delay
        advanceTimeBy(100)
        assertEquals(2, fakeApi.callCount)

        // Complete all coroutines
        advanceUntilIdle()

        // State should reflect the second call's data
        assertEquals("weekly", viewModel.uiState.value.selectedPeriod)
        assertEquals("Repo for weekly", viewModel.uiState.value.repos[0].repoName)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun testRefreshState() = runTest {
        val fakeApi = FakeTrendingApi(responseDelay = 1000)
        val viewModel = MainViewModel(fakeApi)
        advanceUntilIdle()

        // Start refreshing
        viewModel.fetchData(isRefresh = true)
        
        // StandardTestDispatcher requires advanceTimeBy or yield to start the coroutine
        advanceTimeBy(100)
        
        assertTrue(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.isLoading)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
    }
}
