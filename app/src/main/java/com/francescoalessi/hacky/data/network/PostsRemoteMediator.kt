package com.francescoalessi.hacky.data.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.francescoalessi.hacky.api.HackerNewsService
import com.francescoalessi.hacky.data.HackyDatabase
import com.francescoalessi.hacky.model.Post
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ExperimentalPagingApi
class PostsRemoteMediator @Inject
constructor(
    private val hackyDatabase: HackyDatabase,
    private val hackerNewsService: HackerNewsService
) : RemoteMediator<Int, Post>()
{
    private val startPage: Int = 1
    private var currentPage: Int = startPage

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Post>
    ): MediatorResult
    {
        currentPage = when (loadType)
        {
            LoadType.APPEND ->
            {
                // Stop if we reach the end of available content
                if (currentPage >= 10)
                    return MediatorResult.Success(endOfPaginationReached = true)
                else
                    currentPage + 1
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.REFRESH ->
            {
                startPage // When refreshing, we start fetching from the beginning
            }
        }
        try
        {
            val posts = getPostsAndAssignRank()

            hackyDatabase.withTransaction {
                savePostsToDb(posts, loadType)
            }

            return MediatorResult.Success(currentPage >= 10 || posts.isEmpty())
        }
        catch (exception: IOException)
        {
            Log.d("LoadError", "exception: $exception")
            return MediatorResult.Error(exception)
        }
        catch (exception: HttpException)
        {
            Log.d("LoadError", "exception: $exception")
            return MediatorResult.Error(exception)
        }
    }

    suspend fun getPostsAndAssignRank(): List<Post>
    {
        val posts = hackerNewsService.getPosts(currentPage)

        for (i in posts)
        {
            i.rank = posts.indexOf(i) + ((currentPage - 1) * 30)
        }

        return posts
    }

    suspend fun savePostsToDb(posts: List<Post>, loadType: LoadType)
    {
        if (loadType == LoadType.REFRESH)
            hackyDatabase.postDao().deleteAllPosts()
        hackyDatabase.postDao().insertPosts(posts)
    }
}