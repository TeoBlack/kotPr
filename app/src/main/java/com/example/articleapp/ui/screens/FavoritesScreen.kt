package com.example.articleapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.articleapp.ui.navigation.Screen
import com.example.articleapp.ui.viewmodel.ArticleUiEvent
import com.example.articleapp.ui.viewmodel.ArticleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: ArticleViewModel = hiltViewModel()) {
    val favorites by viewModel.favoriteArticles.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Избранное") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.List.route) { popUpTo(Screen.List.route) } }, icon = { Text("Список") }, label = { Text("Список") })
                NavigationBarItem(selected = true, onClick = { }, icon = { Text("Избранное") }, label = { Text("Избранное") })
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.Search.route) }, icon = { Text("Поиск") }, label = { Text("Поиск") })
            }
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Нет избранных статей")
            }
        } else {
            // ВТОРОЙ LAZYCOLUMN
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites, key = { it.id }) { article ->
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