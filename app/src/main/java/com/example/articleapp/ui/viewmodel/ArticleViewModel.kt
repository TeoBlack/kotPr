package com.example.articleapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.articleapp.data.local.ArticleEntity
import com.example.articleapp.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Состояния экрана
sealed class ArticleUiState {
    object Loading : ArticleUiState()
    data class Success(val articles: List<ArticleEntity>) : ArticleUiState()
    data class Error(val message: String) : ArticleUiState()
}

// События
sealed class ArticleUiEvent {
    object Retry : ArticleUiEvent()
    data class ToggleFavorite(val article: ArticleEntity) : ArticleUiEvent()
}

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArticleUiState>(ArticleUiState.Loading)
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    val favoriteArticles: StateFlow<List<ArticleEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun onEvent(event: ArticleUiEvent) {
        when (event) {
            is ArticleUiEvent.Retry -> loadData()
            is ArticleUiEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    repository.toggleFavorite(event.article)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ArticleUiState.Loading

            // Обновляем из сети ТОЛЬКО если база пустая (первый запуск)
            // Если хочешь принудительное обновление - можно вынести в отдельный метод
            if (repository.getArticles().first().isEmpty()) {
                repository.refreshArticles()
            }

            repository.getArticles().collect { articles ->
                if (articles.isEmpty()) {
                    _uiState.value = ArticleUiState.Error("Нет данных. Проверьте интернет.")
                } else {
                    _uiState.value = ArticleUiState.Success(articles)
                }
            }
        }
    }

    // Функция для поиска
    fun search(query: String): Flow<List<ArticleEntity>> {
        return repository.searchArticles(query)
    }
}