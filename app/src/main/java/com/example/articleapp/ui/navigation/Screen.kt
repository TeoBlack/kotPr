package com.example.articleapp.ui.navigation

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object Detail : Screen("detail/{articleId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
}