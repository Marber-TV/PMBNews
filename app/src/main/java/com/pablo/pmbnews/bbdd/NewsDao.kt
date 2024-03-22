package com.pablo.pmbnews.bbdd

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {
    @Query("SELECT * FROM articles_fav")
    fun getSavedNews(): LiveData<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFav(Article: Article)

    @Query("SELECT * FROM articles_fav WHERE title = :title LIMIT 1")
    suspend fun getArticlesFavByTitle(title: String): Article?

    @Insert
    suspend fun insertComment(comment: Comment)

    @Query("SELECT * FROM comments_table WHERE articleTitle = :title ORDER BY timestamp DESC")
    fun getCommentsForArticleByTitle(title: String): LiveData<List<Comment>>

}
