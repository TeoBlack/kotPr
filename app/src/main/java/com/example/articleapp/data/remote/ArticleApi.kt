package com.example.articleapp.data.remote

import retrofit2.http.GET

interface ArticleApi {
    @GET("posts")
    suspend fun getArticles(): List<ArticleDto>
}