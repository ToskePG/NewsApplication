package com.example.mvvmappclass.repository

import com.example.mvvmappclass.api.RetrofitInstance
import com.example.mvvmappclass.db.ArticleDatabase
import com.example.mvvmappclass.model.Article
import retrofit2.Retrofit

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(
        countryCode: String, pageNumber: Int
    ) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(
        query: String, pageNumber: Int
    ) = RetrofitInstance.api.searchForNews(searchQuery = query, pageNumber = pageNumber)


    suspend fun upsert(article: Article) = db.getArticleDao().upsertArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)


}