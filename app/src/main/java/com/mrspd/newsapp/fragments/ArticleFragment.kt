package com.mrspd.newsapp.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mrspd.newsapp.NewsViewModel
import com.mrspd.newsapp.R
import com.mrspd.newsapp.ui.NewsActivity
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    val args :ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)

            fab.setOnClickListener {
                viewModel.upsert(article)
                Snackbar.make(view , " Article has been saved ", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}