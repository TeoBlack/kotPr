package com.example.articleapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.articleapp.data.local.ArticleEntity
import com.example.articleapp.ui.navigation.Screen
import com.example.articleapp.ui.viewmodel.ArticleUiEvent
import com.example.articleapp.ui.viewmodel.ArticleViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: ArticleViewModel = hiltViewModel()) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ArticleEntity>>(emptyList()) }

    // При изменении запроса, обновляем результаты
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
        } else {
            viewModel.search(query).collect { results = it }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Поиск") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.List.route) { popUpTo(Screen.List.route) } }, icon = { Text("Список") }, label = { Text("Список") })
                NavigationBarItem(selected = false, onClick = { navController.navigate(Screen.Favorites.route) }, icon = { Text("Избранное") }, label = { Text("Избранное") })
                NavigationBarItem(selected = true, onClick = { }, icon = { Text("Поиск") }, label = { Text("Поиск") })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Введите название или текст") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (results.isEmpty() && query.isNotBlank()) {
                Text("Ничего не найдено", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results, key = { it.id }) { article ->
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