package com.pablo.pmbnews.bbdd

import androidx.lifecycle.LiveData

class NewsRepository(private val newsDao: NewsDao) {
    val allFav: LiveData<List<Article>> = newsDao.getSavedNews()
    suspend fun insert(Article: Article) {
        newsDao.insertFav(Article)
    }
    suspend fun getArticlesFavByTitle(title: String): Article? {
        return newsDao.getArticlesFavByTitle(title)
    }

    suspend fun insertComment(comment: Comment) {
        newsDao.insertComment(comment)
    }

    // Obtiene los comentarios para un artículo por título
    fun getCommentsForArticleByTitle(title: String): LiveData<List<Comment>> {
        return newsDao.getCommentsForArticleByTitle(title)
    }
}
