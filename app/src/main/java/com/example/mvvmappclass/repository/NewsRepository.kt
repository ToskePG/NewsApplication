package com.example.mvvmappclass.repository

import com.example.mvvmappclass.api.RetrofitInstance
import com.example.mvvmappclass.db.ArticleDatabase
import retrofit2.Retrofit

class NewsRepository(
    val db : ArticleDatabase
) {

    suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)



}