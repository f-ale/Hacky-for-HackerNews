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

class CommentViewModel @Inject constructor(val repository: HackyRepository) : ViewModel()
{
    @ExperimentalPagingApi
    fun getCommentsForThread(threadId:Int): Flow<PagingData<Comment>>
    {
        return repository.getCommentsForThread(threadId).cachedIn(viewModelScope)
    }

    fun getPost(postId:Int) : LiveData<Post>
    {
        return repository.getPost(postId)
    }
}