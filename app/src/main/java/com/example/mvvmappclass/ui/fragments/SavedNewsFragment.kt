package com.example.mvvmappclass.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmappclass.R
import com.example.mvvmappclass.adapters.NewsAdapter
import com.example.mvvmappclass.databinding.FragmentSavedNewsBinding
import com.example.mvvmappclass.model.Article
import com.example.mvvmappclass.ui.NewsActivity
import com.example.mvvmappclass.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSavedNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener { article->
            navigateToArticle(article)
        }
        
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val message = getString(R.string.successfully_deleted_this_article)
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                message.showSnackBar(view, article)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner) { articles ->
            newsAdapter.differ.submitList(articles)
        }

    }


    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun String.showSnackBar(view: View, article: Article){
        Snackbar.make(view, this, Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                viewModel.saveArticle(article)
            }
            show()
        }
    }

    private fun navigateToArticle(article: Article){
        val message = getString(R.string.no_additional_data_for_this_article)
        if(article.description == null ||
            article.url == null || article.content == null || article.title == null){
            message.showToast(requireContext())
        }else{
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(R.id.savedToSingleArticle, bundle)
        }
    }

    private fun String.showToast(context: Context){
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}