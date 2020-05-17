package com.mrspd.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mrspd.newsapp.models.Article


// Data Access Object Class for Room Database
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long


    // SQL queries
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}