package com.example.articleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.articleapp.data.local.ArticleEntity
import com.example.articleapp.ui.navigation.Screen
import com.example.articleapp.ui.viewmodel.ArticleUiEvent
import com.example.articleapp.ui.viewmodel.ArticleUiState
import com.example.articleapp.ui.viewmodel.ArticleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController, viewModel: ArticleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Все статьи") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = { }, icon = { Text("Список") }, label = { Text("Список") })
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.Favorites.route) }, icon = { Text("Избранное") }, label = { Text("Избранное") })
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.Search.route) }, icon = { Text("Поиск") }, label = { Text("Поиск") })
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is ArticleUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is ArticleUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            }
            is ArticleUiState.Success -> {
                // НАШ ПЕРВЫЙ LAZYCOLUMN
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.articles, key = { it.id }) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { navController.navigate(Screen.Detail.createRoute(article.id)) },
                            onFavoriteClick = { viewModel.onEvent(ArticleUiEvent.ToggleFavorite(article)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: ArticleEntity, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = article.title, style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (article.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Избранное",
                    tint = if (article.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}