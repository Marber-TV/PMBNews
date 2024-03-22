package com.pablo.pmbnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pablo.pmbnews.R
import com.pablo.pmbnews.bbdd.Article

class NewsAdapterFav(
    private var articles: List<Article>,
    private val onClick: (Article) -> Unit
) : RecyclerView.Adapter<NewsAdapterFav.NewsViewHolder>() {

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item_fav, parent, false)
        return NewsViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    class NewsViewHolder(itemView: View, val onClick: (Article) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val newsImageView: ImageView = itemView.findViewById(R.id.newsImageView)
        private val newsTitleTextView: TextView = itemView.findViewById(R.id.newsTitleTextView)
        private val newsDateTextView: TextView = itemView.findViewById(R.id.newsDateTextView)

        fun bind(article: Article) {
            newsTitleTextView.text = article.title
            newsDateTextView.text = article.publishedAt
            // Utiliza Glide o Picasso para cargar la imagen desde la URL.
            Glide.with(itemView.context).load(article.urlToImage).error(R.drawable._404).into(newsImageView)

            itemView.setOnClickListener {
                onClick(article)
            }
        }
    }
}
