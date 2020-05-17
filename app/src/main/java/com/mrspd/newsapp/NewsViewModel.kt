package com.mrspd.newsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrspd.newsapp.models.Article
import com.mrspd.newsapp.models.NewsResponse
import com.mrspd.newsapp.repository.NewsRepository
import com.mrspd.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    var newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPageNumber = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchingNewsPageNumber = 1
    var searchNewsResponse: NewsResponse? = null


    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countrycode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countrycode, breakingNewsPageNumber)
        breakingNews.postValue(handleBreakingNewsResponse(response))

    }


    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchingNewsPageNumber)
        //handle search response
        searchNews.postValue(handleBreakingNewsResponse(response))

    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPageNumber++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldarticles = breakingNewsResponse?.articles
                    val newarticles = resultResponse.articles
                    oldarticles?.addAll(newarticles)
                }

                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())

    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchingNewsPageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldarticles = searchNewsResponse?.articles
                    val newarticles = resultResponse.articles
                    oldarticles?.addAll(newarticles)
                }

                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())

    }

    //Room Databse Ke liye
    fun upsert(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getsavedarticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }


}