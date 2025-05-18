package upworksolutions.themagictricks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import upworksolutions.themagictricks.model.Trick
import upworksolutions.themagictricks.repository.TricksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val featuredTricks: List<Trick> = emptyList(),
    val tricksByCategory: Map<String, List<Trick>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastDocumentIds: Map<String, String> = emptyMap(),
    val hasMoreTricks: Map<String, Boolean> = emptyMap()
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tricksRepository: TricksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var currentCategory: String? = null
    private var retryCount = 0
    private val MAX_RETRIES = 3

    init {
        refreshFeaturedTricks()
    }

    fun refreshFeaturedTricks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            retryCount = 0
            loadFeaturedTricks()
        }
    }

    private suspend fun loadFeaturedTricks() {
        try {
            tricksRepository.getFeaturedTricks().collect { tricks ->
                _uiState.update { 
                    it.copy(
                        featuredTricks = tricks,
                        isLoading = false,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun loadMoreTricks(category: String) {
        if (_uiState.value.isLoading) return
        
        viewModelScope.launch {
            currentCategory = category
            _uiState.update { it.copy(isLoading = true, error = null) }
            retryCount = 0
            loadTricksByCategory(category)
        }
    }

    private suspend fun loadTricksByCategory(category: String) {
        try {
            tricksRepository.getTricksByCategory(category).collect { tricks ->
                _uiState.update {
                    it.copy(
                        tricksByCategory = it.tricksByCategory + (category to tricks),
                        isLoading = false,
                        error = null
                    )
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleError(error: Exception) {
        if (retryCount < MAX_RETRIES) {
            retryCount++
            viewModelScope.launch {
                // Exponential backoff
                kotlinx.coroutines.delay(1000L * (1 shl retryCount))
                if (currentCategory != null) {
                    loadTricksByCategory(currentCategory!!)
                } else {
                    loadFeaturedTricks()
                }
            }
        } else {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = error.message ?: "An error occurred"
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 