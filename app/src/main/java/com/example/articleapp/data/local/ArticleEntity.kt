package com.example.articleapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false,
    val imageUrl: String = "https://picsum.photos/seed/$id/400/300" // Картинка-заглушка
)