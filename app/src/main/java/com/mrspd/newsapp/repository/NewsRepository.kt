package com.mrspd.newsapp.repository

import com.mrspd.newsapp.api.RetrofitInstance
import com.mrspd.newsapp.db.ArticleDatabase
import com.mrspd.newsapp.models.Article

//This is repository class as name suggests LOL
class NewsRepository(
    var db: ArticleDatabase
) {
    //asynchronous function using coroutine to make api call
    suspend fun getBreakingNews(countrycode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countrycode, pageNumber)
    // All magic happens in the above line :)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    //Now for ROOM functions
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)
    suspend fun delete(article: Article) = db.getArticleDao().deleteArticle(article)
    fun getsavedarticles() = db.getArticleDao().getAllArticles()
}