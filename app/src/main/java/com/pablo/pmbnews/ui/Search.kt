package com.pablo.pmbnews.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pablo.pmbnews.Extensions
import com.pablo.pmbnews.R
import com.pablo.pmbnews.adapters.NewsAdapter
import com.pablo.pmbnews.bbdd.Article
import com.pablo.pmbnews.bbdd.NewsDatabase
import com.pablo.pmbnews.bbdd.NewsRepository
import com.pablo.pmbnews.bbdd.NewsViewModel
import com.pablo.pmbnews.bbdd.NewsViewModelFactory
import com.pablo.pmbnews.databinding.ActivitySearchBinding
import com.pablo.pmbnews.servicio.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupRecyclerView()
        setupSearch()
    }

    private fun setupViewModel() {

        val application = this.application
        val newsDao = NewsDatabase.getDatabase(this).newsDao()
        val repository = NewsRepository(newsDao)
        val factory = NewsViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(
            emptyList(),
            onClick = { article ->
                val intent = Intent(this@Search, DetailActivity::class.java).apply {
                    putExtra("article", article)
                }
                startActivity(intent)
            }
        ) { article ->
            viewModel.alreadyFav(article.title).observe(this@Search) { exists ->
                if (!exists) {
                    viewModel.insert(article)
                    Toast.makeText(this@Search, "Artículo agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Search, "Este artículo ya está en favoritos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Search)
            adapter = newsAdapter
        }
    }


    private fun setupSearch() {
        binding.searchView.editText?.doAfterTextChanged { text ->
            fetchNews(text.toString())
        }
    }


    private fun fetchNews(query: String) {
        if (query.isBlank()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = Service.create().getAllNews(
                    query,
                    1,
                    Extensions.API_KEY
                )
                if (response.isSuccessful) {
                    newsAdapter.updateData(response.body()?.articles ?: emptyList())
                } else {
                    Log.e(
                        "SearchActivity",
                        "Error fetching news: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: HttpException) {
                Log.e("SearchActivity", "HTTP error fetching news", e)
            } catch (e: Throwable) {
                Log.e("SearchActivity", "Error fetching news", e)
            }
        }
    }
}
