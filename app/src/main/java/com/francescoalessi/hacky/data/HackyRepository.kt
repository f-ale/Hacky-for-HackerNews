package com.francescoalessi.hacky.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.francescoalessi.hacky.data.network.CommentsRemoteMediator
import com.francescoalessi.hacky.data.network.PostsRemoteMediator
import com.francescoalessi.hacky.model.Comment
import com.francescoalessi.hacky.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HackyRepository
@ExperimentalPagingApi
@Inject constructor(
    private val hackyDatabase: HackyDatabase,
    private val postsMediator: PostsRemoteMediator,
    private val commentsMediator: CommentsRemoteMediator
)
{
    fun getPosts(): Flow<PagingData<Post>>
    {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            config = PagingConfig(pageSize = 30),
            remoteMediator = postsMediator,
            pagingSourceFactory = { hackyDatabase.postDao().getPosts() }
        ).flow
    }

    fun getPost(postId: Int): LiveData<Post>
    {
        return hackyDatabase.postDao().getPostForId(postId)
    }

    @ExperimentalPagingApi
    fun getCommentsForThread(threadId: Int): Flow<PagingData<Comment>>
    {
        commentsMediator.threadId = threadId
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            config = PagingConfig(pageSize = 30),
            remoteMediator = commentsMediator,
            pagingSourceFactory = { hackyDatabase.postDao().getCommentsForThread(threadId) }
        ).flow
    }
}