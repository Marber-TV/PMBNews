package com.pablo.pmbnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pablo.pmbnews.R
import com.pablo.pmbnews.databinding.NewsItemBinding
import com.pablo.pmbnews.bbdd.Article

class NewsAdapter(
    private var articles: List<Article>,
    private val onClick: (Article) -> Unit,
    private val onFavoriteClick: (Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding, onClick, onFavoriteClick)
    }

    fun updateData(newData: List<Article>) {
        this.articles = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    class ArticleViewHolder(
        private val binding: NewsItemBinding,
        private val onClick: (Article) -> Unit,
        private val onFavoriteClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentArticle: Article? = null

        init {
            binding.root.setOnClickListener {
                currentArticle?.let { onClick(it) }
            }
            binding.favoriteButton.setOnClickListener {
                currentArticle?.let {
                    onFavoriteClick(it)
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_red)
                }
            }
        }

        fun bind(article: Article) {
            currentArticle = article
            with(binding) {
                newsTitleTextView.text = article.title
                newsDateTextView.text = article.publishedAt
                Glide.with(newsImageView.context)
                    .load(article.urlToImage)
                    .error(R.drawable._404)
                    .into(newsImageView)
            }

        }

    }
}
