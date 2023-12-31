package com.example.mvvmappclass.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmappclass.R
import com.example.mvvmappclass.adapters.NewsAdapter
import com.example.mvvmappclass.databinding.FragmentSearchNewsBinding
import com.example.mvvmappclass.model.Article
import com.example.mvvmappclass.ui.NewsActivity
import com.example.mvvmappclass.ui.NewsViewModel
import com.example.mvvmappclass.util.Constants
import com.example.mvvmappclass.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.mvvmappclass.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSearchNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                if (editable.toString().isNotEmpty()) {
                    viewModel.searchNews(editable.toString())
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        showToast(requireContext(), "An error has occurred $errorMessage")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        newsAdapter.setOnItemClickListener { article ->
            navigateToArticle(article)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private var isLoading = false

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToArticle(article: Article){
        if(article.url == null){
            showToast(requireContext(), "Can't access this article right now")
        }else if(article.content == null || article.title == null
            || article.content == "" ||  article.title == "" || article.description == null
            || article.urlToImage == null || article.urlToImage =="" || article.source == null){
            showToast(requireContext(), "No additional info about this article")
        }else{
            val message = getString(R.string.no_additional_data_for_this_article)
            article.source.id = 0
            if(article.description == null ||
                article.url == null || article.content == null || article.title == null){
                message.showToast(requireContext())
            }else{
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }
                findNavController().navigate(R.id.searchToSingleArticle, bundle)
            }
        }
    }

    private fun String.showToast(context: Context){
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}