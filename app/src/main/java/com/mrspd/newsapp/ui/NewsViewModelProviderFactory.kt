package com.mrspd.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mrspd.newsapp.NewsViewModel
import com.mrspd.newsapp.repository.NewsRepository

class NewsViewModelProviderFactory(
    var newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }
}