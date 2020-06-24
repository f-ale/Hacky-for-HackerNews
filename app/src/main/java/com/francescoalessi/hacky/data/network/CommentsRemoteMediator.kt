package com.francescoalessi.hacky.data.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.francescoalessi.hacky.api.HackerNewsService
import com.francescoalessi.hacky.data.HackyDatabase
import com.francescoalessi.hacky.model.Comment
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ExperimentalPagingApi
class CommentsRemoteMediator @Inject
constructor(
    private val hackyDatabase: HackyDatabase,
    private val hackerNewsService: HackerNewsService
) : RemoteMediator<Int, Comment>()
{
    var threadId: Int = 23553325
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Comment>
    ): MediatorResult
    {
        when (loadType)
        {
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> return MediatorResult.Success(true)
            LoadType.REFRESH ->
                try
                {
                    // Retrieve thread
                    val thread = hackerNewsService.getThread(threadId)
                    // Unpack comments into a linear structure
                    val comments: MutableList<Comment> = unpackComments(
                        thread.comments.toMutableList(),
                        ArrayList(thread.comments_count)
                    )

                    // If this is an AskHN thread, we add the thread content as a comment to the comment list
                    if (!thread.content.isNullOrEmpty())
                    {
                        val ask = Comment(
                            thread.id * 10,
                            thread.id,
                            0,
                            thread.user,
                            thread.time ?: 0,
                            thread.content,
                            null,
                            0
                        )

                        comments.add(0, ask)
                    }
                    // Set rank and parentId for comments
                    setMetadata(comments, threadId)

                    hackyDatabase.withTransaction {
                        // If we're refreshing, we delete stale comments first
                        if (loadType == LoadType.REFRESH)
                            hackyDatabase.postDao().deleteCommentsForThread(threadId)
                        hackyDatabase.postDao().insertComments(comments)
                    }
                    return MediatorResult.Success(true)
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
    }

    // Turns nested structure into linear
    private fun unpackComments(
        sourceList: MutableList<Comment>,
        targetList: MutableList<Comment>
    ): MutableList<Comment>
    {
        for (comment in sourceList)
        {
            targetList.add(comment)
            if (!comment.comments.isNullOrEmpty())
            {
                unpackComments(comment.comments!!.toMutableList(), targetList)
            }
        }

        return targetList
    }

    private fun setMetadata(comments: List<Comment>, threadId: Int)
    {
        for (comment in comments)
        {
            comment.parentId = threadId
            comment.rank = comments.indexOf(comment)
        }
    }
}