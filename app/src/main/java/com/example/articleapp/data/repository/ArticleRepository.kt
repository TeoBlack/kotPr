package com.example.articleapp.data.repository

import com.example.articleapp.data.local.ArticleDao
import com.example.articleapp.data.local.ArticleEntity
import com.example.articleapp.data.remote.ArticleApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val api: ArticleApi,
    private val dao: ArticleDao
) {
    // Чтение из БД (всегда, когда UI просит данные)
    fun getArticles(): Flow<List<ArticleEntity>> = dao.getAllArticles()
    fun getFavorites(): Flow<List<ArticleEntity>> = dao.getFavoriteArticles()
    fun searchArticles(query: String): Flow<List<ArticleEntity>> = dao.searchArticles(query)
    suspend fun getArticleById(id: Int): ArticleEntity? = dao.getArticleById(id)

    // Переключение избранного
    suspend fun toggleFavorite(article: ArticleEntity) {
        dao.updateFavoriteStatus(article.id, !article.isFavorite)
    }

    // Синхронизация с сервером (Offline-first)
    // Синхронизация с сервером (Offline-first)
    suspend fun refreshArticles(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. Получаем список ID избранных статей из БД
            val favoriteIds = dao.getFavoriteIds()

            // 2. Скачиваем новые с сервера
            val remoteArticles = api.getArticles()

            // 3. Мапим, но сохраняем статус избранного
            val entities = remoteArticles.map { dto ->
                ArticleEntity(
                    id = dto.id,
                    title = dto.title,
                    body = dto.body,
                    isFavorite = favoriteIds.contains(dto.id) // Сохраняем сердечко!
                )
            }
            dao.insertArticles(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}