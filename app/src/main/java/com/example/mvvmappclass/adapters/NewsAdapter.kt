package com.example.mvvmappclass.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmappclass.R
import com.example.mvvmappclass.databinding.ItemArticlePreviewBinding
import com.example.mvvmappclass.model.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(
        val binding: ItemArticlePreviewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) = bindArticle(differ.currentList[position], holder)

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private fun bindArticle(article: Article, holder: ArticleViewHolder){
        val message = "No information about source of this article. "
        holder.binding.apply {
            article.urlToImage?.let { imageUrl ->
                Glide.with(ivArticleImage.context).load(imageUrl).into(ivArticleImage)
            } ?: ivArticleImage.setImageResource(R.drawable.news)
            tvSource.text = article.source?.name ?: message
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }
}