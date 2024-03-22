package com.pablo.pmbnews.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pablo.pmbnews.R
import com.pablo.pmbnews.bbdd.Article

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        val articleImageView = findViewById<ImageView>(R.id.articleImage)
        val articleTitleTextView = findViewById<TextView>(R.id.tvTitle)
        val articleSourceTextView = findViewById<TextView>(R.id.tvSource)
        val articleDescriptionTextView = findViewById<TextView>(R.id.tvDescription)
        val articlePublishedAtTextView = findViewById<TextView>(R.id.tvPublishedAt)
        val more = findViewById<TextView>(R.id.readMoreButton)


        val article = intent.getSerializableExtra("article") as? Article

        article?.let {
            Glide.with(this).load(it.urlToImage).error(R.drawable._404).into(articleImageView)
            articleTitleTextView.text = it.title
            articleSourceTextView.text = it.source?.name
            articleDescriptionTextView.text = it.description
            articlePublishedAtTextView.text = it.publishedAt
            more.setOnClickListener {
                article.url?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
        }


        findViewById<ImageView>(R.id.ivArrow).setOnClickListener {
            onBackPressed()
        }
    }
}
