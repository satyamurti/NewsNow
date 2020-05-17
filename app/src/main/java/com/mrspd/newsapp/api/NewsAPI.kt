package com.mrspd.newsapp.api

import com.mrspd.newsapp.models.NewsResponse
import com.mrspd.newsapp.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {


    //Used coroutine for running these queries in background thread

    //Retrofit Query For Getting BreakingNews
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String ="us",
        @Query("page")
        pageNumber : Int= 1,
        @Query("apikey")
        apikey : String = API_KEY
    ): Response<NewsResponse>


    //Retrofit Query For Searching All News
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String ,
        @Query("page")
        pageNumber : Int= 1,
        @Query("apikey")
        apikey : String = API_KEY
    ): Response<NewsResponse>

}