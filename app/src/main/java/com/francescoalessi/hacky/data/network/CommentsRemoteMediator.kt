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
constructor(val hackyDatabase: HackyDatabase,
            val hackerNewsService: HackerNewsService) : RemoteMediator<Int, Comment>()
{
    var threadId:Int = 23553325
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Comment>
    ): MediatorResult
    {
        when(loadType)
        {
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> return MediatorResult.Success(true)
            LoadType.REFRESH ->
                try
                {
                    val thread = hackerNewsService.getThread(threadId)
                    val comments : List<Comment> = unpackComments(thread.comments.toMutableList(), ArrayList<Comment>(thread.comments_count))
                    setMetadata(comments, threadId)

                    hackyDatabase.withTransaction {
                        if(loadType == LoadType.REFRESH)
                            hackyDatabase.postDao().deleteCommentsForThread(threadId)
                        hackyDatabase.postDao().insertComments(comments)
                    }
                    return MediatorResult.Success(true)
                }
                catch (exception: IOException) {
                    Log.d("LoadError", "exception: $exception")
                    return MediatorResult.Error(exception)
                } catch (exception: HttpException) {
                    Log.d("LoadError", "exception: $exception")
                    return MediatorResult.Error(exception)
                }
        }
    }

    // Turns nested structure into linear
    fun unpackComments(sourceList: MutableList<Comment>, targetList: MutableList<Comment>) : MutableList<Comment>
    {
        for(comment in sourceList)
        {
            targetList.add(comment)
            if(!comment.comments.isNullOrEmpty())
            {
                unpackComments(comment.comments!!.toMutableList(), targetList)
            }
        }

        return targetList
    }

    fun setMetadata(comments: List<Comment>, threadId:Int)
    {
        for(comment in comments)
        {
            comment.parentId = threadId
            comment.rank = comments.indexOf(comment)
        }
    }
}