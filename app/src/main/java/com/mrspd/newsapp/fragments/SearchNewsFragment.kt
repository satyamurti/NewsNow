package com.mrspd.newsapp.fragments


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrspd.newsapp.NewsViewModel
import com.mrspd.newsapp.R
import com.mrspd.newsapp.adapters.NewsAdapter
import com.mrspd.newsapp.ui.NewsActivity
import com.mrspd.newsapp.utils.Constants
import com.mrspd.newsapp.utils.Constants.Companion.SEARCH_DELAY
import com.mrspd.newsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()


        newsAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )

        }

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->

            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY)
                editable?.let {
                    if (!editable.toString().isEmpty()) {

                        viewModel.searchNews(editable.toString())

                    }
                }
            }

        }








        viewModel.searchNews.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsresponse ->
                        newsAdapter.differ.submitList(newsresponse.articles.toList())
                        val totalPages = newsresponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPageNumber == totalPages
                        if (isLastPage) {
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error -> {

                    hideProgressBar()
                    response.message?.let {
                        Log.d("BreakingNewsFragment", "This is   $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleCount = layoutManager.childCount
            val totaItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPagee = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleCount >= totaItemCount
            val isTotalMoreThanVisible = totaItemCount >= Constants.QUERY_PAGE_SIZE
            val shouildPaginate =
                isNotLoadingAndNotLastPagee && isAtLastItem && isTotalMoreThanVisible
            if (shouildPaginate) {
                viewModel.searchNews(etSearch.text.toString())
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

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        var isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        var isLoading = true
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {

            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }


    }
}