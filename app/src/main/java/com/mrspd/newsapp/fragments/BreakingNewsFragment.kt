package com.mrspd.newsapp.fragments

import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrspd.newsapp.NewsViewModel
import com.mrspd.newsapp.R
import com.mrspd.newsapp.adapters.NewsAdapter
import com.mrspd.newsapp.ui.NewsActivity
import com.mrspd.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.mrspd.newsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()


        // passing article as a argument through navigation components
        newsAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )

        }



        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let { newsresponse ->
                        newsAdapter.differ.submitList(newsresponse.articles.toList())
                        val totalPages = newsresponse.totalResults / QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.breakingNewsPageNumber == totalPages
                        if (isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {

                    hideProgressBar()
                    response.message?.let {
                        d("BreakingNewsFragment", "This is   $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLastPage = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLastPage = true

    }


    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    // For pagging
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleCount = layoutManager.childCount
            val totaItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPagee = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleCount >= totaItemCount
            val isTotalMoreThanVisible = totaItemCount>= QUERY_PAGE_SIZE
            val shouildPaginate = isNotLoadingAndNotLastPagee && isAtLastItem && isTotalMoreThanVisible
            if (shouildPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

    }
}