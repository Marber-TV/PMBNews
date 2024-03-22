package com.pablo.pmbnews.bbdd

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "news_table")
data class News(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String,
    val urlToImage: String
)

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

@Entity(tableName = "comments_table")
data class Comment(
    @PrimaryKey(autoGenerate = true) val commentId: Int = 0,
    val articleTitle: String,
    val username: String,
    val content: String,
    val timestamp: Date
)

@Entity(tableName = "articles_fav")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val source: Source?,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
): Serializable

data class Source(
    val id: String?,
    val name: String?
): Serializable

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> error(message: String, data: T? = null): Resource<T> =
            Resource(Status.ERROR, data, message)

        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data, null)
    }
}
