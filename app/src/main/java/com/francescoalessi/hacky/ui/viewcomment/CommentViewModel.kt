package com.francescoalessi.hacky.ui.viewcomment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.francescoalessi.hacky.data.HackyRepository
import com.francescoalessi.hacky.model.Comment
import com.francescoalessi.hacky.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentViewModel @Inject constructor(private val repository: HackyRepository) : ViewModel()
{
    var currentThreadId:Int = -1
    lateinit var comment: Flow<PagingData<Comment>>

    @ExperimentalPagingApi
    fun getCommentsForThread(threadId: Int): Flow<PagingData<Comment>>
    {
        if(threadId != currentThreadId)
        {
            currentThreadId = threadId
            comment = repository.getCommentsForThread(threadId).cachedIn(viewModelScope)
        }

        return comment
    }

    fun getPost(postId: Int): LiveData<Post>
    {
        return repository.getPost(postId)
    }
}