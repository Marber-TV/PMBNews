package com.pablo.pmbnews.bbdd

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pablo.pmbnews.Extensions
import com.pablo.pmbnews.servicio.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(application: Application, private val repository: NewsRepository) :
    ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    var context = application.applicationContext
    private val _newsState = MutableLiveData<Resource<List<Article>>>()
    val newsState: LiveData<Resource<List<Article>>> = _newsState
    private val _newsArticles = MutableLiveData<List<Article>>()
    val newsArticles: LiveData<List<Article>> = _newsArticles
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    val allFav: LiveData<List<Article>> = repository.allFav // Para las noticias guardadas
    fun insert(article: Article) = viewModelScope.launch {
        repository.insert(article)
    }

    fun alreadyFav(title: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val article = withContext(Dispatchers.IO) {
                repository.getArticlesFavByTitle(title)
            }
            result.postValue(article != null)
        }
        return result
    }


    fun fetchNews(query: String = "España") {
        viewModelScope.launch {
            _newsState.value = Resource.loading(null)
            try {
                val service = Service.create()
                val response = withContext(Dispatchers.IO) {
                    service.getAllNews(query, 1, Extensions.API_KEY)
                }
                if (response.isSuccessful && response.body() != null) {
                    _newsArticles.postValue(response.body()!!.articles) // Actualiza las noticias de la API
                    _newsState.postValue(Resource.success(response.body()!!.articles))
                } else {
                    _newsState.postValue(Resource.error("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                _newsState.postValue(Resource.error("Exception: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCommentsForArticle(articleTitle: String): LiveData<List<Comment>> {
        return repository.getCommentsForArticleByTitle(articleTitle)
    }


    fun addComment(comment: Comment) {
        viewModelScope.launch {
            repository.insertComment(comment)
            val updatedComments = _comments.value.orEmpty() + comment
            _comments.value = updatedComments
        }
    }

}

class NewsViewModelFactory(
    private val application: Application,
    private val repository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



