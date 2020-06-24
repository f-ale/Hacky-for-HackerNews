package com.francescoalessi.hacky.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.francescoalessi.hacky.model.Comment
import com.francescoalessi.hacky.model.Post

@Dao
interface PostDao
{
    @Query("SELECT * FROM posts ORDER BY rank")
    fun getPosts(): PagingSource<Int, Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("SELECT * FROM posts WHERE id == :postId")
    fun getPostForId(postId: Int): LiveData<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comment: List<Comment>)

    @Query("DELETE FROM comments WHERE parentId == :threadId")
    suspend fun deleteCommentsForThread(threadId: Int)

    @Query("SELECT * FROM comments WHERE comments.parentId == :threadId ORDER BY rank")
    fun getCommentsForThread(threadId: Int): PagingSource<Int, Comment>
}